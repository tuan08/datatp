package net.datatp.webcrawler.fetcher;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.http.crawler.urldb.URLDatum;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLDatumFetchQueue {
  static int MAX_QUEUE_CAPACITY = 250000 ;

  private LinkedBlockingQueue<URLDatum> scheduleQueue = new LinkedBlockingQueue<URLDatum>(MAX_QUEUE_CAPACITY) ;
  private LinkedBlockingQueue<BusyURLDatum> busyQueue = new LinkedBlockingQueue<BusyURLDatum>(3000);;

  public void add(URLDatum urldatum) throws InterruptedException {
    if(scheduleQueue.size() == MAX_QUEUE_CAPACITY) {
      Thread.sleep(1000) ;
    }
    scheduleQueue.put(urldatum) ;
  }

  public void addBusy(URLDatum urldatum) throws InterruptedException {
    busyQueue.put(new BusyURLDatum(urldatum)) ;
  }

  public URLDatum poll(long maxWait) throws InterruptedException {
    BusyURLDatum busyUrldatum = busyQueue.poll() ;
    URLDatum urldatum = null ;
    if(busyUrldatum != null) {
      if(System.currentTimeMillis() > busyUrldatum.expireTime) {
        urldatum = busyUrldatum.urldatum ;
      } else {
        busyQueue.put(busyUrldatum) ;
      }
    }
    if(urldatum == null) {
      urldatum = scheduleQueue.poll(maxWait, TimeUnit.MILLISECONDS) ;
    }
    return urldatum ;
  }

  static class BusyURLDatum {
    URLDatum urldatum ;
    long     expireTime ;

    BusyURLDatum(URLDatum urldatum) {
      this.urldatum = urldatum ;
      this.expireTime = System.currentTimeMillis() + 1000 ;
    }

  }
}
