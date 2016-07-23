package net.datatp.http.crawler.scheduler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;

public class InMemURLPreFetchScheduler extends URLPreFetchScheduler {
  private BlockingQueue<URLDatum> urlFetchQueue;
  
  public InMemURLPreFetchScheduler(URLDatumDB urlDatumDB, BlockingQueue<URLDatum> urlFetchQueue) {
    this.urlDatumDB    = urlDatumDB;
    this.urlFetchQueue = urlFetchQueue;
  }
  
  @Override
  protected void onSchedule(ArrayList<URLDatum> holder) throws Exception {
    for(int i = 0; i < holder.size(); i++) {
      urlFetchQueue.offer(holder.get(i), 5, TimeUnit.SECONDS);
    }
  }

}
