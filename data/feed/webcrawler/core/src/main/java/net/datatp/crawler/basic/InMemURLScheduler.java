package net.datatp.crawler.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.scheduler.SchedulerReporter;
import net.datatp.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.crawler.scheduler.URLPreFetchScheduler;
import net.datatp.crawler.scheduler.URLScheduler;
import net.datatp.crawler.scheduler.URLSchedulerPluginManager;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumDB;

public class InMemURLScheduler extends URLScheduler {
  public InMemURLScheduler(URLDatumDB urlDatumDB, 
                           SiteContextManager siteContextManager,
                           BlockingQueue<URLDatum> urlFetchQueue,
                           BlockingQueue<URLDatum> urlCommitQueue) {
    preFetchScheduler  = new InMemURLPreFetchScheduler(urlDatumDB, siteContextManager, urlFetchQueue);
    postFetchScheduler = new InMemURLPostFetchScheduler(urlDatumDB, siteContextManager, urlCommitQueue);
    reporter           = new InMemSchedulerReporter();
  }
  
  public class InMemURLPreFetchScheduler extends URLPreFetchScheduler {
    private BlockingQueue<URLDatum> urlFetchQueue;
    
    public InMemURLPreFetchScheduler(URLDatumDB urlDatumDB, 
                                     SiteContextManager siteContextManager,
                                     BlockingQueue<URLDatum> urlFetchQueue) {
      this.urlDatumDB    = urlDatumDB;
      this.siteContextManager = siteContextManager;
      this.schedulerPluginManager = new URLSchedulerPluginManager();
      this.urlFetchQueue = urlFetchQueue;
    }
    
    @Override
    protected void onSchedule(ArrayList<URLDatum> holder) throws Exception {
      for(int i = 0; i < holder.size(); i++) {
        urlFetchQueue.offer(holder.get(i), 5, TimeUnit.SECONDS);
      }
    }
  }
  
  public class InMemURLPostFetchScheduler extends URLPostFetchScheduler {
    private BlockingQueue<URLDatum> urlCommitQueue;
    
    public InMemURLPostFetchScheduler(URLDatumDB urlDatumDB, 
                                      SiteContextManager siteContextManager, 
                                      BlockingQueue<URLDatum> urlCommitQueue) {
      this.urlDatumDB         = urlDatumDB;
      this.siteContextManager = siteContextManager;
      this.schedulerPluginManager = new URLSchedulerPluginManager();
      this.urlCommitQueue     = urlCommitQueue;
    }
    
    @Override
    public URLCommitMetric process() throws Exception {
      List<URLDatum> holder = new ArrayList<>();
      urlCommitQueue.drainTo(holder, 10000);
      schedule(holder);
      return super.process();
    }
    
  }
  
  public class InMemSchedulerReporter implements SchedulerReporter {
    private Map<Long, URLScheduleMetric> urlScheduleMetrics = new MaxSizeLinkedHashMap<>(500);
    private Map<Long, URLCommitMetric>   urlCommitMetrics   = new MaxSizeLinkedHashMap<>(500);
    
    @Override
    public void report(URLScheduleMetric metric) throws Exception {
      if(metric == null) return;
      urlScheduleMetrics.put(metric.getTime(), metric);
    }

    @Override
    public void report(URLCommitMetric metric) throws Exception {
      if(metric == null) return;
      urlCommitMetrics.put(metric.getTime(), metric);
    }

    @Override
    public List<URLCommitMetric> getURLCommitReport(int max) throws Exception {
      List<URLCommitMetric> holder = new ArrayList<>();
      holder.addAll(urlCommitMetrics.values());
      Collections.reverse(holder);
      return holder;
    }

    @Override
    public List<URLScheduleMetric> getURLScheduleReport(int max) throws Exception {
      List<URLScheduleMetric> holder = new ArrayList<>();
      holder.addAll(urlScheduleMetrics.values());
      Collections.reverse(holder);
      return holder;
    }
  }
  
  
  static public class MaxSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
   
    private final int maxSize;

    public MaxSizeLinkedHashMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
}
