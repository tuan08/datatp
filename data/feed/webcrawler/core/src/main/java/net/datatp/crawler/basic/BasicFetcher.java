package net.datatp.crawler.basic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import net.datatp.crawler.fetcher.URLFetcher;
import net.datatp.crawler.fetcher.URLFetcherReport;
import net.datatp.crawler.fetcher.Fetcher;
import net.datatp.crawler.fetcher.FetcherReport;
import net.datatp.crawler.fetcher.Status;
import net.datatp.crawler.fetcher.FetcherStatus;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.MD5;
import net.datatp.xhtml.XDoc;

public class BasicFetcher implements Fetcher {
  private CrawlerConfig      crawlerConfig;
  private SiteSessionManager siteSessionManager = new SiteSessionManager();

  private URLFetcher[]       urlFetchers;
  private Thread[]           fetcherThreads;
  private FetcherStatus      status;
  
  public BasicFetcher(CrawlerConfig config,
                      BlockingQueue<URLDatum> urlFetchQueue, 
                      BlockingQueue<URLDatum> urlCommitQueue,
                      BlockingQueue<XDoc>     xDocQueue,
                      FetchDataProcessor      dataProcessor,
                      SiteContextManager      siteContextManager) throws UnknownHostException {
    crawlerConfig = config;
    urlFetchers   = new URLFetcher[crawlerConfig.getNumOfFetcher()];
    for(int i = 0; i < urlFetchers.length; i++) {
      String name = "url-fetcher-" + (i);
      urlFetchers[i] = new BasicURLFetcher(name, siteContextManager, siteSessionManager, urlFetchQueue, urlCommitQueue, xDocQueue, dataProcessor);
    }
    String host = InetAddress.getLocalHost().getHostName();
    status = new FetcherStatus(MD5.digest(host + hashCode()).toString(), host);
  }
  
  public FetcherStatus getStatus() { return this.status ; }
  
  public FetcherReport getFetcherReport() {
    FetcherReport report = new FetcherReport();
    report.setStatus(status);
    URLFetcherReport[] urlFetcherReport = new URLFetcherReport[urlFetchers.length];
    for(int i = 0; i < urlFetchers.length; i++) {
      urlFetcherReport[i] = urlFetchers[i].getReport();
    }
    report.setUrlFetcherReport(urlFetcherReport);
    return report;
  }
  
  public void start() {
    status.setStatus(Status.FETCHING);
    fetcherThreads = new Thread[urlFetchers.length];
    for(int i = 0; i < fetcherThreads.length; i++) {
      fetcherThreads[i] = new Thread(urlFetchers[i]);
      fetcherThreads[i].start();
    }
  }
  
  public void stop() {
    status.setStatus(Status.STOP);
    if(fetcherThreads == null) return;
    for(int i = 0; i < fetcherThreads.length; i++) {
      urlFetchers[i].setExit(true);
      fetcherThreads[i].interrupt();
    }
    fetcherThreads = null;
  }
}
