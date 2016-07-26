package net.datatp.crawler.basic;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.processor.XhtmlDocumentProcessor;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.InMemURLDatumDB;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumFactory;
import net.datatp.xhtml.XhtmlDocument;

public class Crawler implements CrawlerApi {
  private CrawlerConfig                crawlerConfig;

  private BlockingQueue<URLDatum>      urlFetchQueue;
  private BlockingQueue<URLDatum>      urlCommitQueue;
  private BlockingQueue<XhtmlDocument> xhtmlDocumentQueue;

  private SiteContextManager           siteContextManager     = new SiteContextManager();

  private HttpFetcherManager           httpFetcherManager;

  private InMemURLDatumDB              urlDatumDB;
  private InMemURLScheduler            urlScheduler;

  private InMemFetchDataProcessor      dataProcessor;

  private XhtmlDocumentProcessor       xhtmlDocumentProcessor = XhtmlDocumentProcessor.NONE;
  private XhtmlDocumentProcessorThread xhtmlDocumentProcessorThread;
  
  public Crawler configure(CrawlerConfig config) throws Exception {
    crawlerConfig      = config;
    
    urlFetchQueue      = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    urlCommitQueue     = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    xhtmlDocumentQueue = new LinkedBlockingQueue<>(crawlerConfig.getMaxXhtmlDocumentQueueSize());
    
    URLExtractor urlExtractor = new URLExtractor(URLDatumFactory.DEFAULT, CrawlerConfig.EXCLUDE_URL_PATTERNS);
    dataProcessor = new InMemFetchDataProcessor(siteContextManager, urlExtractor, urlCommitQueue, xhtmlDocumentQueue);
    
    httpFetcherManager = 
      new HttpFetcherManager(crawlerConfig, urlFetchQueue, urlCommitQueue, dataProcessor, siteContextManager);
    
    urlDatumDB   = new InMemURLDatumDB();
    urlScheduler = new InMemURLScheduler(urlDatumDB, siteContextManager, urlFetchQueue, urlCommitQueue);
    
    xhtmlDocumentProcessorThread = new XhtmlDocumentProcessorThread();
    xhtmlDocumentProcessorThread.start();
    return this;
  }
  
  @Override
  public void siteCreateGroup(String group) throws Exception { }

  @Override
  public void siteAdd(SiteConfig config) throws Exception {
    siteContextManager.addConfig(config);
  }
  
  @Override
  public List<SiteConfig> siteGetSiteConfigs() { return siteContextManager.getSiteConfigs(); }
  
  @Override
  public void siteReload() throws Exception { }
  
  @Override
  public List<URLCommitMetric> schedulerGetURLCommitReport(int max) throws Exception {
    return urlScheduler.getSchedulerReporter().getURLCommitReport(max);
  }

  @Override
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(int max) throws Exception {
    return urlScheduler.getSchedulerReporter().getURLScheduleReport(max);
  }

  @Override
  public void schedulerStart() throws Exception {
    urlScheduler.injectURL();
    urlScheduler.start();
  }

  @Override
  public void schedulerStop() throws Exception {
    urlScheduler.stop();
  }

  @Override
  public void fetcherStart() throws Exception {
    httpFetcherManager.start();
  }

  @Override
  public void fetcherStop() throws Exception {
    httpFetcherManager.stop();
  }
  
  public void setXhtmlDocumentProcessor(XhtmlDocumentProcessor processor) {
    xhtmlDocumentProcessor = processor;
  }
  
  public void start() throws Exception {
    schedulerStart();
    fetcherStart();
  }
  
  public void stop() throws Exception {
    schedulerStop();
    fetcherStop();
  }
  
  public class XhtmlDocumentProcessorThread extends Thread {
    boolean terminate ;
    public void run() {
      while(!terminate) {
        try {
          XhtmlDocument xdoc = xhtmlDocumentQueue.poll(1, TimeUnit.SECONDS);
          if(xdoc != null) xhtmlDocumentProcessor.process(xdoc);
        } catch(InterruptedException ex) {
        } catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
