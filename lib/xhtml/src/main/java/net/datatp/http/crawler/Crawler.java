package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.site.SiteConfig;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;

public class Crawler {
  private CrawlerConfig            crawlerConfig;

  private BlockingQueue<URLDatum>  urlFetchQueue;
  private BlockingQueue<URLDatum>  urlCommitQueue;
  private BlockingQueue<FetchData> dataFetchQueue;

  private SiteContextManager       siteContextManager = new SiteContextManager();
  private HttpFetcherManager       httpFetcherManager;
  private URLDatumDB               urlDatumDB;

  public Crawler addSiteConfig(SiteConfig config) {
    siteContextManager.addConfig(config);
    return this;
  }
  
  public Crawler configure(CrawlerConfig config) {
    crawlerConfig      = config;
    
    urlFetchQueue      = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    urlCommitQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    dataFetchQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxDataFetchQueueSize());
    
    httpFetcherManager = new HttpFetcherManager(crawlerConfig, urlFetchQueue, urlCommitQueue, dataFetchQueue, siteContextManager);
    
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
