package net.datatp.webcrawler.master;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.URLDatum;
import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.webcrawler.master.model.URLCommitInfo;
import net.datatp.webcrawler.site.SiteContext;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.site.SiteScheduleStat;
import net.datatp.webcrawler.site.URLContext;
import net.datatp.webcrawler.urldb.URLDatumDB;
import net.datatp.webcrawler.urldb.URLDatumRecord;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 29, 2010  
 */
@Component
@ManagedResource(
  objectName="org.headvances.crawler.fetch:name=URLPostFetchScheduler", 
  description="This bean is responsible to commit the downloaded urls"
)
public class URLPostFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPostFetchScheduler.class);

  @Autowired
  private URLDatumDB urlDatumDB ;
  @Autowired
  private SiteContextManager siteConfigManager ;
  @Autowired
  protected URLFetchSchedulerPlugins fetcherPlugins ;
  
  private LinkedBlockingQueue<URLDatum> commitURLDatumQueue = new LinkedBlockingQueue<URLDatum>(10000);
  private Map<Text, URLDatumEntry>      newURLDatums = new ConcurrentHashMap<Text, URLDatumEntry>() ;	

  private int maxWaitTime = 3000 ;

  public URLDatumDB getURLDatumDB() { return this.urlDatumDB ; }

  public int getMaxWaitTime() { return this.maxWaitTime ; }
  public void setMaxWaitTime(int value) { this.maxWaitTime = value ; }

  public SiteContextManager getSiteConfigManager() { return this.siteConfigManager ; }

  @JmsListener(destination = "crawler.url.fetch.commit")
  public void schedule(List<URLDatum> urls) throws InterruptedException {
    for(int i = 0; i < urls.size(); i++) {
      URLDatum datum = urls.get(i) ;
      if(datum.getStatus() == URLDatum.STATUS_NEW) {
        URLDatumEntry entry = newURLDatums.get(datum.getId()) ;
        if(entry == null) {
          newURLDatums.put(new Text(datum.getId()), new URLDatumEntry(datum)) ;
        } else {
          entry.hit++ ;
        }
      } else {
        commitURLDatumQueue.put(datum) ;
      }
    }
  }

  public URLCommitInfo process() throws Exception {
    int inQueue = siteConfigManager.getInQueueCount() ;
    int maxProcess = inQueue ;
    if(maxProcess > 100) maxProcess = maxProcess/2 ;
    logger.info("Start processing date!!!!!!  MaxWaitTime = " + maxWaitTime + "ms # MaxProcess = " + maxProcess + " # In Queue " + inQueue) ;

    long startTime = System.currentTimeMillis() ;
    SortKeyValueFile<Text, URLDatumRecord>.Writer writer = null ;
    int processCount = 0, newURLFoundCount = 0, newURLTypeList =0, newURLTypeDetail = 0 ;

    int noneDequeueCount = 0 ;
    while(processCount < maxProcess) {
      URLDatumRecord[] urls = dequeueCommitURLDatums();
      if(urls.length == 0) {
        noneDequeueCount++ ;
        if(noneDequeueCount == 100) break ;
        Thread.sleep(500) ;
        continue ;
      } else {
        if(writer == null) writer = urlDatumDB.newSegment().getWriter() ;
        processCount += write(writer, urls) ;
      }
      
      urls = dequeueNewURLDatums(250000);
      if(urls.length > 0) {
        if(writer == null) writer = urlDatumDB.newSegment().getWriter() ;
        newURLFoundCount += write(writer, urls) ;
      }
    }

    if(writer != null) {
      URLDatumRecord[] urls = dequeueNewURLDatums(0);
      newURLFoundCount += write(writer, urls) ;
      writer.close() ;
      urlDatumDB.autoCompact() ;
    }
    
    if(processCount == 0 && newURLFoundCount == 0) return null;
    
    logger.info("Process {} fetch request, found {} new urls!", processCount, newURLFoundCount) ;
    long execTime = System.currentTimeMillis() - startTime ;
    URLCommitInfo info = 
      new URLCommitInfo(startTime, execTime, processCount, newURLFoundCount, newURLTypeList, newURLTypeDetail) ;
    return info ;
  }

  private URLDatumRecord[] dequeueCommitURLDatums() throws InterruptedException {
    List<URLDatumRecord> urls = new ArrayList<URLDatumRecord>() ;
    URLDatumRecord datum = null;
    while((datum = (URLDatumRecord)commitURLDatumQueue.poll()) != null) {
      urls.add(datum) ;
    }
    return urls.toArray(new URLDatumRecord[urls.size()]) ;
  }

  private URLDatumRecord[] dequeueNewURLDatums(int keep) throws InterruptedException {
    List<URLDatumRecord> urls = new ArrayList<URLDatumRecord>() ;
    int hit = 1 ;
    while(newURLDatums.size() > keep) {
      Iterator<URLDatumEntry> i = newURLDatums.values().iterator() ;
      while(newURLDatums.size() > keep && i.hasNext()) {
        URLDatumEntry entry = i.next() ;
        if(entry.hit <= hit) {
          urls.add((URLDatumRecord)entry.urldatum) ;
          i.remove();
        }
      }
      hit++ ;
    }
    return urls.toArray(new URLDatumRecord[urls.size()]) ;
  }

  private int write(SortKeyValueFile<Text, URLDatumRecord>.Writer writer, URLDatumRecord[] urls) throws Exception {
    for(int i = 0 ; i < urls.length; i++) {
      URLDatumRecord urlDatum = urls[i] ;
      URLContext context = siteConfigManager.getURLContext(urlDatum.getOriginalUrlAsString()) ;
      if(context == null) {
        logger.warn("SiteConfig is not found for the url ", urlDatum.getOriginalUrlAsString()) ;
        continue ;
      }
      SiteContext siteConfigContext = context.getSiteContext() ;
      if(urlDatum.getStatus() != URLDatum.STATUS_NEW) {
        fetcherPlugins.postFetch(context, urlDatum, System.currentTimeMillis()) ;
        SiteScheduleStat scheduleStat = siteConfigContext.getAttribute(SiteScheduleStat.class) ;
        scheduleStat.addProcessCount(1) ;
      } else {
      }
      writer.append(new Text(urlDatum.getId()), urlDatum) ;
    }
    return urls.length ;
  }

  static class URLDatumEntry {
    int hit  ;
    URLDatum urldatum ;

    URLDatumEntry(URLDatum urldatum) {
      this.urldatum = urldatum ;
      this.hit = 1 ;
    }
  }
}