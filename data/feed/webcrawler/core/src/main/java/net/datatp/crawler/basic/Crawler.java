package net.datatp.crawler.basic;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.processor.XDocProcessor;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.InMemURLDatumDB;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumFactory;
import net.datatp.xhtml.XDoc;

public class Crawler implements CrawlerApi {
  private CrawlerConfig           crawlerConfig;

  private BlockingQueue<URLDatum> urlFetchQueue;
  private BlockingQueue<URLDatum> urlCommitQueue;
  private BlockingQueue<XDoc>     xDocQueue;

  private SiteContextManager      siteContextManager = new SiteContextManager();

  private HttpFetcherManager      httpFetcherManager;

  private InMemURLDatumDB         urlDatumDB;
  private InMemURLScheduler       urlScheduler;

  private FetchDataProcessor      dataProcessor;

  private XDocProcessor           xDocProcessor      = XDocProcessor.NONE;
  private XDocProcessorThread     xDocProcessorThread;

  public Crawler configure(CrawlerConfig config) throws Exception {
    crawlerConfig      = config;
    
    urlFetchQueue  = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    urlCommitQueue = new LinkedBlockingQueue<>(crawlerConfig.getMaxUrlQueueSize());
    xDocQueue      = new LinkedBlockingQueue<>(crawlerConfig.getMaxXDocQueueSize());
    
    URLExtractor urlExtractor = new URLExtractor(URLDatumFactory.DEFAULT, CrawlerConfig.EXCLUDE_URL_PATTERNS);
    dataProcessor = new FetchDataProcessor(urlExtractor);
    
    httpFetcherManager = 
      new HttpFetcherManager(crawlerConfig, urlFetchQueue, urlCommitQueue, xDocQueue, dataProcessor, siteContextManager);
    
    urlDatumDB   = new InMemURLDatumDB();
    urlScheduler = new InMemURLScheduler(urlDatumDB, siteContextManager, urlFetchQueue, urlCommitQueue);
    
    xDocProcessorThread = new XDocProcessorThread();
    xDocProcessorThread.start();
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
  
  public void setXDocProcessor(XDocProcessor processor) {
    xDocProcessor = processor;
  }
  
  public void start() throws Exception {
    schedulerStart();
    fetcherStart();
  }
  
  public void stop() throws Exception {
    schedulerStop();
    fetcherStop();
  }
  
  public class XDocProcessorThread extends Thread {
    boolean terminate ;
    public void run() {
      while(!terminate) {
        try {
          XDoc xdoc = xDocQueue.poll(1, TimeUnit.SECONDS);
          if(xdoc != null) {
            xDocProcessor.process(xdoc);
          }
        } catch(InterruptedException ex) {
        } catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
