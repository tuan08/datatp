package net.datatp.http.crawler.scheduler;

import java.util.concurrent.BlockingQueue;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;

public class CrawlerURLScheduler {
  private URLDatumDB              db;
  private BlockingQueue<URLDatum> urlFetchQueue;
  private BlockingQueue<URLDatum> urlCommitQueue;
  
  public CrawlerURLScheduler(URLDatumDB db, 
                      BlockingQueue<URLDatum> urlFetchQueue, 
                      BlockingQueue<URLDatum>  urlCommitQueue) {
    this.db = db;
    this.urlFetchQueue  = urlFetchQueue;
    this.urlCommitQueue = urlCommitQueue;
  }
}
