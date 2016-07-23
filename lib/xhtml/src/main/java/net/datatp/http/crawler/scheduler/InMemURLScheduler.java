package net.datatp.http.crawler.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.http.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.http.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;

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

    @Override
    public void report(URLScheduleMetric info) throws Exception {
    }

    @Override
    public void report(URLCommitMetric info) throws Exception {
    }
  }
}
