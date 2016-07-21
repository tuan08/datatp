package net.datatp.http.crawler.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.http.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.http.crawler.site.SiteContext;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.site.SiteScheduleStat;
import net.datatp.http.crawler.site.URLContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDBIterator;
import net.datatp.http.crawler.urldb.URLDatumDBWriter;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
abstract public class URLPostFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPostFetchScheduler.class);

  protected SiteContextManager siteContextManager ;
  
  protected URLSchedulerPluginManager schedulerPluginManager ;
  
  protected LinkedBlockingQueue<URLDatum> commitURLDatumQueue = new LinkedBlockingQueue<URLDatum>(10000);
  protected Map<String, URLDatumEntry>      newURLDatums = new ConcurrentHashMap<String, URLDatumEntry>() ;	

  private int maxWaitTime = 3000 ;

  public int getMaxWaitTime() { return this.maxWaitTime ; }
  public void setMaxWaitTime(int value) { this.maxWaitTime = value ; }

  public SiteContextManager getSiteConfigManager() { return this.siteContextManager ; }

  abstract public URLDatumDBIterator createURLDatumDBIterator() throws Exception ;
  
  abstract public URLDatumDBWriter createURLDatumDBWriter() throws Exception ;
  
  public URLCommitMetric process() throws Exception {
    int inQueue = siteContextManager.getInQueueCount() ;
    int maxProcess = inQueue ;
    if(maxProcess > 100) maxProcess = maxProcess/2 ;
    logger.info("Start processing date!!!!!!  MaxWaitTime = " + maxWaitTime + "ms # MaxProcess = " + maxProcess + " # In Queue " + inQueue) ;

    long startTime = System.currentTimeMillis() ;
    URLDatumDBWriter writer = null ;
    int processCount = 0, newURLFoundCount = 0, newURLTypeList =0, newURLTypeDetail = 0 ;

    int noneDequeueCount = 0 ;
    while(processCount < maxProcess) {
      URLDatum[] urls = dequeueCommitURLDatums();
      if(urls.length == 0) {
        noneDequeueCount++ ;
        if(noneDequeueCount == 100) break ;
        Thread.sleep(500) ;
        continue ;
      } else {
        if(writer == null) writer = createURLDatumDBWriter() ;
        processCount += write(writer, urls) ;
      }
      
      urls = dequeueNewURLDatums(250000);
      if(urls.length > 0) {
        if(writer == null) writer = createURLDatumDBWriter() ;
        newURLFoundCount += write(writer, urls) ;
      }
    }

    if(writer != null) {
      URLDatum[] urls = dequeueNewURLDatums(0);
      newURLFoundCount += write(writer, urls) ;
      writer.close() ;
      writer.optimize() ;
    }
    
    if(processCount == 0 && newURLFoundCount == 0) return null;
    
    logger.info("Process {} fetch request, found {} new urls!", processCount, newURLFoundCount) ;
    long execTime = System.currentTimeMillis() - startTime ;
    URLCommitMetric info = 
      new URLCommitMetric(startTime, execTime, processCount, newURLFoundCount, newURLTypeList, newURLTypeDetail) ;
    return info ;
  }

  private URLDatum[] dequeueCommitURLDatums() throws InterruptedException {
    List<URLDatum> urls = new ArrayList<URLDatum>() ;
    URLDatum datum = null;
    while((datum = commitURLDatumQueue.poll()) != null) {
      urls.add(datum) ;
    }
    return urls.toArray(new URLDatum[urls.size()]) ;
  }

  private URLDatum[] dequeueNewURLDatums(int keep) throws InterruptedException {
    List<URLDatum> urls = new ArrayList<URLDatum>() ;
    int hit = 1 ;
    while(newURLDatums.size() > keep) {
      Iterator<URLDatumEntry> i = newURLDatums.values().iterator() ;
      while(newURLDatums.size() > keep && i.hasNext()) {
        URLDatumEntry entry = i.next() ;
        if(entry.hit <= hit) {
          urls.add(entry.urldatum) ;
          i.remove();
        }
      }
      hit++ ;
    }
    return urls.toArray(new URLDatum[urls.size()]) ;
  }

  private int write(URLDatumDBWriter writer, URLDatum[] urls) throws Exception {
    for(int i = 0 ; i < urls.length; i++) {
      URLDatum urlDatum = urls[i] ;
      URLContext context = siteContextManager.getURLContext(urlDatum.getOriginalUrlAsString()) ;
      if(context == null) {
        logger.warn("SiteConfig is not found for the url ", urlDatum.getOriginalUrlAsString()) ;
        continue ;
      }
      SiteContext siteConfigContext = context.getSiteContext() ;
      if(urlDatum.getStatus() != URLDatum.STATUS_NEW) {
        schedulerPluginManager.postFetch(context, urlDatum, System.currentTimeMillis()) ;
        SiteScheduleStat scheduleStat = siteConfigContext.getAttribute(SiteScheduleStat.class) ;
        scheduleStat.addProcessCount(1) ;
      } else {
      }
      writer.write(urlDatum) ;
    }
    return urls.length ;
  }

  static public class URLDatumEntry {
    public int hit  ;
    public URLDatum urldatum ;

    public URLDatumEntry(URLDatum urldatum) {
      this.urldatum = urldatum ;
      this.hit = 1 ;
    }
  }
}