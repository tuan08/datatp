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
public class URLDatumFetchBufferQueue {
  static int MAX_QUEUE_CAPACITY = 150000 ;

  private BlockingQueue<URLDatum>      scheduleQueue;
  private BlockingQueue<DelayURLDatum> delayURLFetchQueue = new DelayQueue<>();
  
  public URLDatumFetchBufferQueue() {
    scheduleQueue = new LinkedBlockingQueue<URLDatum>(MAX_QUEUE_CAPACITY);
  }
  
  public URLDatumFetchBufferQueue(BlockingQueue<URLDatum> scheduleQueue) {
    this.scheduleQueue = scheduleQueue;
  }
  
  public void add(URLDatum urldatum) throws InterruptedException {
    scheduleQueue.put(urldatum);
  }

  public void addBusy(URLDatum urldatum) throws InterruptedException {
    delayURLFetchQueue.offer(new DelayURLDatum(urldatum, 3000));
  }

  public URLDatum poll(long maxWait) throws InterruptedException {
    DelayURLDatum delayURLDatum =  delayURLFetchQueue.poll();
    if(delayURLDatum != null) return delayURLDatum.getURLDatum();
    return scheduleQueue.poll(maxWait, TimeUnit.MILLISECONDS);
  }
  
  public long getInQueue() { return scheduleQueue.size() ; }
  
  public long getInDelayQueue() { return delayURLFetchQueue.size(); }

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