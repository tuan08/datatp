package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;

import net.datatp.xhtml.XhtmlDocument;

public class HttpFetcherManager {
  private CrawlerConfig                crawlerConfig ;
  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<URLDatum>      urlCommitQueue;
  private BlockingQueue<XhtmlDocument> fetchDataQueue;
  
  public HttpFetcherManager(CrawlerConfig config,
                            BlockingQueue<URLDatum> urlFetchQueue, 
                            BlockingQueue<URLDatum> urlCommitQueue,
                            BlockingQueue<XhtmlDocument> dataFetchQueue) {
    this.crawlerConfig      = config;
    this.urlFetchQueue      = urlFetchQueue;
    this.urlCommitQueue     = urlCommitQueue;
    this.fetchDataQueue     = dataFetchQueue;
  }
  
  public void start() {
    
  }
  
  public void stop() {
    
  }
  
  
}
