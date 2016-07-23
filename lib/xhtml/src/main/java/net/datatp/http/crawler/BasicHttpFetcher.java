package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.fetcher.HttpFetcher;
import net.datatp.http.crawler.fetcher.SiteSessionManager;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;

public class BasicHttpFetcher extends HttpFetcher {
  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<FetchData>     fetchDataQueue;
  
  public BasicHttpFetcher(String name, 
                          SiteContextManager manager, 
                          SiteSessionManager siteSessionManager, 
                          BlockingQueue<URLDatum>  urlFetchQueue,
                          BlockingQueue<FetchData> fetchDataQueue) {
    super(name, manager, siteSessionManager);
    this.urlFetchQueue  = urlFetchQueue;
    this.fetchDataQueue = fetchDataQueue;
  }

  @Override
  protected void onCommit(FetchData fdata) throws Exception {
    fetchDataQueue.offer(fdata, 5, TimeUnit.SECONDS);
  }

  @Override
  protected void onDelay(URLDatum urlDatum) throws InterruptedException {
    urlFetchQueue.offer(urlDatum, 5, TimeUnit.SECONDS);
  }

  @Override
  protected URLDatum nextURLDatum(long maxWaitTime) throws Exception {
    return urlFetchQueue.poll(1, TimeUnit.SECONDS);
  }
}
