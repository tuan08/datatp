package net.datatp.crawler.fetcher;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.urldb.URLDatum;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLFetchQueue {

  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<DelayURLDatum> urlDelayFetchQueue = new DelayQueue<>();
  private long enqueueCount = 0;
  private long enqueueDelayCount = 0;
  
  public URLFetchQueue(int maxQueueSize) {
    urlFetchQueue = new LinkedBlockingQueue<URLDatum>(maxQueueSize);
  }
  
  public void add(URLDatum urldatum) throws InterruptedException {
    enqueueCount++;
    urlFetchQueue.put(urldatum);
  }

  public void addBusy(URLDatum urldatum) throws InterruptedException {
    enqueueDelayCount++;
    urlDelayFetchQueue.offer(new DelayURLDatum(urldatum, 3000));
  }

  public URLDatum poll(long maxWait) throws InterruptedException {
    DelayURLDatum delayURLDatum =  urlDelayFetchQueue.poll();
    if(delayURLDatum != null) return delayURLDatum.getURLDatum();
    return urlFetchQueue.poll(maxWait, TimeUnit.MILLISECONDS);
  }
  
  public URLFetchQueueReport getURLFetchQueueReport() {
    URLFetchQueueReport report = new URLFetchQueueReport();
    report.setInQueue(urlFetchQueue.size());
    report.setEnqueue(enqueueCount);
    report.setInDelayQueue(urlDelayFetchQueue.size());
    report.setEnqueueDelay(enqueueDelayCount);
    return report;
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