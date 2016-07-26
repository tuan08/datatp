package net.datatp.crawler.basic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.fetcher.HttpFetcher;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;

public class BasicHttpFetcher extends HttpFetcher {
  private BlockingQueue<URLDatum>  urlFetchQueue;
  private FetchDataProcessor       dataProcessor;
  
  public BasicHttpFetcher(String name, 
                          SiteContextManager manager, 
                          SiteSessionManager siteSessionManager, 
                          BlockingQueue<URLDatum>  urlFetchQueue,
                          FetchDataProcessor       dataProcessor) {
    super(name, manager, siteSessionManager);
    this.urlFetchQueue  = urlFetchQueue;
    this.dataProcessor  = dataProcessor;
  }

  @Override
  protected void onProcess(FetchData fdata) throws Exception {
    dataProcessor.process(fdata);
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
