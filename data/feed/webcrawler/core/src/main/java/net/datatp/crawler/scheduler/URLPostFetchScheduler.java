package net.datatp.crawler.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.NewURLDatumCache;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumDB;
import net.datatp.crawler.urldb.URLDatumDBWriter;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
abstract public class URLPostFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPostFetchScheduler.class);

  protected URLDatumDB                 urlDatumDB;
  protected SiteContextManager         siteContextManager;

  protected URLSchedulerPluginManager  schedulerPluginManager;

  protected BlockingQueue<URLDatum>    commitURLDatumQueue = new LinkedBlockingQueue<URLDatum>(10000);
  protected NewURLDatumCache           newURLDatumCache        = new NewURLDatumCache(25000);

  private int                          maxProcessTime      = 30000;

  public URLDatumDB getURLDatumDB() { return this.urlDatumDB; }
  
  public SiteContextManager getSiteContextManager() { return siteContextManager ; }

  public void schedule(List<URLDatum> urls) throws InterruptedException {
    for(int i = 0; i < urls.size(); i++) {
      URLDatum datum = urls.get(i) ;
      if(datum.getStatus() == URLDatum.STATUS_NEW) {
        newURLDatumCache.add(datum) ;
      } else {
        commitURLDatumQueue.offer(datum, 3000, TimeUnit.SECONDS) ;
      }
    }
  }
  
  public URLCommitMetric process() throws Exception {
    logger.info("Start processing date!!!!!!  MaxProcessTime = " + maxProcessTime + "ms") ;

    long startTime = System.currentTimeMillis() ;
    URLDatumDBWriter writer = null ;
    int processCount = 0, newURLFoundCount = 0 ;
    long expiredTime = System.currentTimeMillis() + maxProcessTime;
    while(System.currentTimeMillis() < expiredTime) {
      List<URLDatum> commitURLDatums = new ArrayList<URLDatum>() ;
      commitURLDatumQueue.drainTo(commitURLDatums, 10000);
      if(commitURLDatums.size() > 0) {
        if(writer == null) writer = urlDatumDB.createURLDatumDBWriter() ;
        processCount += write(writer, commitURLDatums) ;
      }

      List<URLDatum> newURLDatums = newURLDatumCache.takeExpiredURLDatums();
      if(newURLDatums.size() > 0) {
        if(writer == null) writer = urlDatumDB.createURLDatumDBWriter() ;
        newURLFoundCount += write(writer, newURLDatums) ;
      }
      if(commitURLDatums.size() == 0 && newURLDatums.size() == 0) break;
    }
    
    List<URLDatum> newURLDatums = newURLDatumCache.takeAllURLDatums();
    if(newURLDatums.size() > 0) {
      if(writer == null) writer = urlDatumDB.createURLDatumDBWriter() ;
      newURLFoundCount += write(writer, newURLDatums) ;
    }
    
    if(writer != null) {
      writer.close() ;
      writer.optimize() ;
    }
    
    if(processCount == 0 && newURLFoundCount == 0) return null;
    
    logger.info("Process {} fetch request, found {} new urls!", processCount, newURLFoundCount) ;
    long execTime = System.currentTimeMillis() - startTime ;
    URLCommitMetric info = new URLCommitMetric(startTime, execTime, processCount, newURLFoundCount) ;
    return info ;
  }

  private int write(URLDatumDBWriter writer, List<URLDatum> urlDatums) throws Exception {
    for(int i = 0 ; i < urlDatums.size(); i++) {
      URLDatum urlDatum = urlDatums.get(i) ;
      URLContext context = siteContextManager.getURLContext(urlDatum) ;
      if(context == null) {
        logger.warn("SiteConfig is not found for the url ", urlDatum.getOriginalUrl()) ;
        continue ;
      }
      SiteContext siteContext = context.getSiteContext() ;
      if(urlDatum.getStatus() != URLDatum.STATUS_NEW) {
        schedulerPluginManager.postFetch(context, urlDatum, System.currentTimeMillis()) ;
        siteContext.getSiteScheduleStat().addProcessCount(1); ;
      } else {
      }
      writer.write(urlDatum) ;
    }
    return urlDatums.size() ;
  }
}