package net.datatp.http.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.processor.InMemFetchDataProcessor;
import net.datatp.http.crawler.processor.URLExtractor;
import net.datatp.http.crawler.scheduler.InMemURLScheduler;
import net.datatp.http.crawler.site.SiteConfig;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumFactory;
import net.datatp.util.log.LoggerFactory;
import net.datatp.http.crawler.urldb.InMemURLDatumDB;

public class Crawler {
  static String[] EXCLUDE_URL_PATTERNS = {
      ".*\\.(pdf|doc|xls|ppt)",
      ".*\\.(rss|rdf)",
      ".*\\.(img|jpg|jpeg|gif|png)",
      ".*\\.(exe)",
      ".*\\.(zip|arj|rar|lzh|z|gz|gzip|tar|bin|rar)" ,
      ".*\\.(mp3|m4a|wav|ra|ram|aac|aif|avi|mpg|mpeg|qt|plj|asf|mov|rm|mp4|wma|wmv|mpe|mpa)",
      ".*\\.(r0*|r1*|a0*|a1*|tif|tiff|msi|msu|ace|iso|ogg|7z|sea|sit|sitx|pps|bz2|xsl)"
  };
  
  private CrawlerConfig            crawlerConfig;

  private BlockingQueue<URLDatum>  urlFetchQueue;
  private BlockingQueue<URLDatum>  urlCommitQueue;
  private BlockingQueue<FetchData> dataFetchQueue;

  private SiteContextManager       siteContextManager = new SiteContextManager();
  
  private HttpFetcherManager       httpFetcherManager;
  
  private InMemURLDatumDB          urlDatumDB;
  private InMemURLScheduler        urlScheduler;
  
  private InMemFetchDataProcessor  dataProcessor ;
  
  public Crawler addSiteConfig(SiteConfig config) {
    siteContextManager.addConfig(config);
    return this;
  }
  
  public Crawler configure(CrawlerConfig config) throws Exception {
    crawlerConfig      = config;
    
    urlFetchQueue      = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    urlCommitQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    dataFetchQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxDataFetchQueueSize());
    
    httpFetcherManager = new HttpFetcherManager(crawlerConfig, urlFetchQueue, urlCommitQueue, dataFetchQueue, siteContextManager);
    
    urlDatumDB   = new InMemURLDatumDB();
    urlScheduler = new InMemURLScheduler(urlDatumDB, siteContextManager, urlFetchQueue, urlCommitQueue);
    
    URLExtractor urlExtractor = new URLExtractor(URLDatumFactory.DEFAULT, EXCLUDE_URL_PATTERNS);
    dataProcessor = new InMemFetchDataProcessor(siteContextManager, urlExtractor, dataFetchQueue, urlCommitQueue);
    urlScheduler.injectURL();
    return this;
  }
  
  public Crawler start() {
    httpFetcherManager.start();
    urlScheduler.start();
    dataProcessor.start();
    return this;
  }
  
  public Crawler stop() {
    httpFetcherManager.stop();
    dataProcessor.stop();
    urlScheduler.stop();
    return this;
  }
  
  static public void main(String[] args) throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    
    Crawler crawler = new Crawler();
    crawler.addSiteConfig(new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3));
    crawler.addSiteConfig(new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3)); 
    crawler.configure(new CrawlerConfig()).start();
    Thread.currentThread().join();
  }
}
