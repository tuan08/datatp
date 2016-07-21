package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.fetcher.HttpFetcher;
import net.datatp.http.crawler.fetcher.SiteSessionManager;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;

public class HttpFetcherManager {
  private CrawlerConfig      crawlerConfig;
  private SiteSessionManager siteSessionManager = new SiteSessionManager();

  private HttpFetcher[]      fetchers;

  private Thread[]           fetcherThreads;
  
  public HttpFetcherManager(CrawlerConfig config,
                            BlockingQueue<URLDatum> urlFetchQueue, 
                            BlockingQueue<URLDatum> urlCommitQueue,
                            BlockingQueue<FetchData> fetchDataQueue, 
                            SiteContextManager siteContextManager) {
    this.crawlerConfig      = config;
    
    fetchers = new HttpFetcher[crawlerConfig.getNumOfFetcher()];
    for(int i = 0; i < fetchers.length; i++) {
      String name = "fetcher-" + (i);
      fetchers[i] = new BasicHttpFetcher(name, siteContextManager, siteSessionManager, urlFetchQueue, fetchDataQueue);
    }
  }
  
  public void start() {
    fetcherThreads = new Thread[fetchers.length];
    for(int i = 0; i < fetcherThreads.length; i++) {
      fetcherThreads[i] = new Thread(fetchers[i]);
      fetcherThreads[i].start();
    }
  }
  
  public void stop() {
    if(fetcherThreads == null) return;
    for(int i = 0; i < fetcherThreads.length; i++) {
      fetchers[i].setExit(true);
      fetcherThreads[i].interrupt();
    }
    fetcherThreads = null;
  }
}
