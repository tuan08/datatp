package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.datatp.xhtml.XhtmlDocument;

public class Crawler {
  private CrawlerConfig                crawlerConfig;

  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<URLDatum>      urlCommitQueue;
  private BlockingQueue<XhtmlDocument> dataFetchQueue;
  
  private HttpFetcherManager           httpFetcherManager;
  private URLDatumDB                   urlDatumDB;

  public Crawler addSiteConfig(SiteConfig config) {
    return this;
  }
  
  public Crawler configure(CrawlerConfig config) {
    crawlerConfig      = config;
    
    urlFetchQueue      = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    urlCommitQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    dataFetchQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxDataFetchQueueSize());
    
    httpFetcherManager = new HttpFetcherManager(crawlerConfig, urlFetchQueue, urlCommitQueue, dataFetchQueue);
    
    urlDatumDB = new URLDatumDB();
    return this;
  }
  
  public Crawler start() {
    httpFetcherManager.start();
    return this;
  }
  
  public Crawler stop() {
    return this;
  }
}
