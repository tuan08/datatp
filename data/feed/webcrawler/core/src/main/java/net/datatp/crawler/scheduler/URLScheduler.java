package net.datatp.crawler.scheduler;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumDBWriter;
import net.datatp.util.URLParser;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLScheduler.class);

  protected URLPreFetchScheduler  preFetchScheduler ;
  
  protected URLPostFetchScheduler postFetchScheduler ;
  
  protected SchedulerReporter     reporter;
  
  protected boolean                          exist = false;
  protected ManageThread                     manageThread;
  protected String                           state       = "INIT";
  protected boolean                          injectUrl = false;

  public URLScheduler() { }
  
  public URLScheduler(URLPreFetchScheduler preFetchScheduler, 
                      URLPostFetchScheduler postFetchScheduler, 
                      SchedulerReporter reporter) { 
    this.preFetchScheduler = preFetchScheduler;
    this.postFetchScheduler = postFetchScheduler;
    this.reporter = reporter;
  }
  
  public SchedulerReporter getSchedulerReporter() { return reporter; }
  
  public String getState() { return this.state ; }

  synchronized public void start() {
    logger.info("start CrawlerURLScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    if(manageThread != null && manageThread.isAlive()) {
      exist = false;
      return ;
    }
    manageThread = new ManageThread() ;
    manageThread.setName("crawler.master.fetch-manager") ;
    state = "STARTING" ;
    manageThread.start() ;
    logger.info("Create a new thread and start CrawlerURLScheduler!") ;
  }

  synchronized public void stop() {
    logger.info("stop CrawlerURLScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    state = "STOPPING" ;
    if(manageThread == null) return ;
    if(manageThread.isAlive()) {
      exist = true ;
      logger.info("set manage thread to exist for CrawlerURLScheduler") ;
    }
  }

  public void run() {
    try {
      long lastUpdateDB = 0l ;
      long updatePeriod =  1 * 24 * 3600 * 1000l ;
      URLCommitMetric commitInfo = null ;
      while(!exist) {
        if(injectUrl || commitInfo == null) {
          state = "SCHEDULING" ;
          URLScheduleMetric sheduleInfo = preFetchScheduler.schedule() ;
          reporter.report(sheduleInfo);
          if(injectUrl) injectUrl = false;
        }
        state = "COMMITING" ;
        commitInfo = postFetchScheduler.process() ;
        reporter.report(commitInfo);
        //        if(lastUpdateDB + updatePeriod < System.currentTimeMillis()) {
        //        	URLDatumRecordDB urldatumDB = postFetchScheduler.getURLDatumDB() ;
        //        	URLDatumDBUpdater updater = new URLDatumDBUpdater(postFetchScheduler.getSiteConfigManager()) ;
        //        	urldatumDB.update(updater) ;
        //        	logger.info("\n" + updater.getUpdateInfo()) ;
        //        	lastUpdateDB = System.currentTimeMillis() ;
        //        }
        Thread.sleep(1000) ;
      }
    } catch(Throwable ex) {
      logger.error("URLDatumgFetchScheduler Error", ex) ;
    }
  }

  public void injectURL() throws Exception {
    URLDatumDBWriter writer = postFetchScheduler.getURLDatumDB().createURLDatumDBWriter();
    
    long currentTime = System.currentTimeMillis() ;
    int count = 0 ;

    SiteContextManager siteContextManager = postFetchScheduler.getSiteContextManager() ;
    Iterator<Map.Entry<String, SiteContext>> i = siteContextManager.getSiteConfigContexts().entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, SiteContext> entry = i.next() ;
      SiteContext context = entry.getValue() ;
      SiteConfig.Status status = context.getSiteConfig().getStatus() ;
      if(status != SiteConfig.Status.Ok) continue ;
      String[] url = context.getSiteConfig().getInjectUrl() ;
      for(String selUrl : url) {
        selUrl = selUrl.trim() ;
        if(selUrl.length() == 0) continue ;
        URLParser newURLParser = new URLParser(selUrl) ;
        URLDatum datum = writer.createURLDatumInstance(currentTime) ;
        datum.setDeep((byte) 1) ;
        datum.setOriginalUrl(selUrl, newURLParser) ;
        datum.setPageType(URLDatum.PAGE_TYPE_LIST) ;
        writer.write(datum);
        count++ ;
      }
    }
    writer.close() ;
    writer.optimize();
    injectUrl = true;
    logger.info("inject/update " + count + " urls") ;
  }

  public class ManageThread extends Thread {
    public void run() { URLScheduler.this.run() ; }
  }
}