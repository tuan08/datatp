package net.datatp.crawler.basic;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.fetcher.URLFetcher;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.XDoc;

public class BasicURLFetcher extends URLFetcher {
  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<URLDatum>      urlCommitQueue;
  private BlockingQueue<XDoc>          xDocQueue;
  private BlockingQueue<DelayURLDatum> delayURLFetchQueue = new DelayQueue<>();
  
  public BasicURLFetcher(String name, 
                         SiteContextManager manager, 
                         SiteSessionManager siteSessionManager, 
                         BlockingQueue<URLDatum>  urlFetchQueue,
                         BlockingQueue<URLDatum>  urlCommitQueue,
                         BlockingQueue<XDoc>      xDocQueue,
                         FetchDataProcessor       dataProcessor) {
    super(name, manager, siteSessionManager, dataProcessor);
    this.urlFetchQueue  = urlFetchQueue;
    this.urlCommitQueue = urlCommitQueue;
    this.xDocQueue      = xDocQueue;
  }

  protected void onCommit(ArrayList<URLDatum> holder) throws Exception {
    for(int i = 0; i < holder.size(); i++) {
      urlCommitQueue.offer(holder.get(i), 5, TimeUnit.SECONDS);
    }
  }

  protected void onCommit(XDoc xDoc) throws Exception {
    xDocQueue.offer(xDoc, 5, TimeUnit.SECONDS);
  }

  
  @Override
  protected void onDelay(URLDatum urlDatum) throws InterruptedException {
    delayURLFetchQueue.offer(new DelayURLDatum(urlDatum, 3000));
  }

  @Override
  protected URLDatum nextURLDatum(long maxWaitTime) throws Exception {
    DelayURLDatum delayURLDatum =  delayURLFetchQueue.poll();
    if(delayURLDatum != null) return delayURLDatum.getURLDatum();
    return urlFetchQueue.poll(maxWaitTime, TimeUnit.MILLISECONDS);
  }
  
  static public class DelayURLDatum implements Delayed  {
    private URLDatum urlDatum;
    private long startTime;
    
    public DelayURLDatum(URLDatum urlDatum, long delay) {
      this.urlDatum  = urlDatum;
      this.startTime = System.currentTimeMillis() + delay;
    }
    
    public URLDatum getURLDatum() { return this.urlDatum; }
    
    @Override
    public long getDelay(TimeUnit unit) {
      long diff = startTime - System.currentTimeMillis();
      return unit.convert(diff, TimeUnit.MILLISECONDS);
    }
    @Override
    public int compareTo(Delayed o) {
      if (startTime < ((DelayURLDatum) o).startTime) return -1;
      else if (startTime > ((DelayURLDatum) o).startTime) return 1; 
      else return 0;
    }
  }
}