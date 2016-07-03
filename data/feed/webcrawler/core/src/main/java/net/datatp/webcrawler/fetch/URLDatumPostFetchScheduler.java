package net.datatp.webcrawler.fetch;

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
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.Segment;
import net.datatp.webcrawler.site.SiteContext;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.site.SiteScheduleStat;
import net.datatp.webcrawler.urldb.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;
import net.datatp.webcrawler.urldb.URLDatumCommitInfo;
import net.datatp.webcrawler.urldb.URLDatumDB;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 29, 2010  
 */
@Component
@ManagedResource(
    objectName="org.headvances.crawler.fetch:name=URLDatumPostFetchScheduler", 
    description="This bean is responsible to commit the downloaded urls"
    )
public class URLDatumPostFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLDatumPostFetchScheduler.class);

  @Autowired
  private URLDatumDB urlDatumDB ;
  @Autowired
  private SiteContextManager siteConfigManager ;
  @Autowired
  protected FetcherPlugins fetcherPlugins ;
  private LinkedBlockingQueue<URLDatum> commitURLDatumQueue = new LinkedBlockingQueue<URLDatum>(10000);
  private Map<Text, URLDatumEntry> newURLDatums = new ConcurrentHashMap<Text, URLDatumEntry>() ;	

  private int maxWaitTime = 3000 ;

  public URLDatumDB getURLDatumDB() { return this.urlDatumDB ; }

  public int getMaxWaitTime() { return this.maxWaitTime ; }
  public void setMaxWaitTime(int value) { this.maxWaitTime = value ; }

  public SiteContextManager getSiteConfigManager() { return this.siteConfigManager ; }

  public void schedule(List<URLDatum> urldatum) throws InterruptedException {
    for(int i = 0; i < urldatum.size(); i++) {
      URLDatum datum = urldatum.get(i) ;
      if(datum.getStatus() == URLDatum.STATUS_NEW) {
        URLDatumEntry entry = newURLDatums.get(datum.getId()) ;
        if(entry == null) {
          newURLDatums.put(datum.getId(), new URLDatumEntry(datum)) ;
        } else {
          entry.hit++ ;
        }
      } else {
        commitURLDatumQueue.put(datum) ;
      }
    }
  }

  public URLDatumCommitInfo process() throws Exception {
    int inQueue = siteConfigManager.getInQueueCount() ;
    int maxProcess = inQueue ;
    if(maxProcess > 100) maxProcess = maxProcess/2 ;
    logger.info(
      "Start processing date!!!!!!  MaxWaitTime = " + maxWaitTime + "ms # MaxProcess = " + maxProcess + 
      " # In Queue " + inQueue
    ) ;

    long startTime = System.currentTimeMillis() ;
    SortKeyValueFile<Text, URLDatum>.Writer writer = null ;
    int processCount = 0, newURLFoundCount = 0, newURLTypeList =0, newURLTypeDetail = 0 ;

    int noneDequeueCount = 0 ;
    while(processCount < maxProcess) {
      URLDatum[] urls = dequeueCommitURLDatums();
      if(urls.length == 0) {
        noneDequeueCount++ ;
        if(noneDequeueCount == 100) break ;
        Thread.sleep(500) ;
        continue ;
      }
      if(writer == null) {
        Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
        writer = segment.getWriter() ;
      }
      processCount += write(writer, urls) ;
      urls = dequeueNewURLDatums(250000);
      newURLFoundCount += write(writer, urls) ;
    }

    if(writer != null) {
      URLDatum[] urls = dequeueNewURLDatums(0);
      newURLFoundCount += write(writer, urls) ;
      writer.close() ;
      urlDatumDB.autoCompact() ;
    }
    logger.info("Process {} fetch request, found {} new urls!", processCount, newURLFoundCount) ;
    long execTime = System.currentTimeMillis() - startTime ;
    URLDatumCommitInfo info = 
        new URLDatumCommitInfo(startTime, execTime, processCount, newURLFoundCount, newURLTypeList, newURLTypeDetail) ;
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

  private int write(SortKeyValueFile<Text, URLDatum>.Writer writer, URLDatum[] urls) throws Exception {
    for(int i = 0 ; i < urls.length; i++) {
      URLDatum urlDatum = urls[i] ;
      URLContext context = siteConfigManager.getURLContext(urlDatum.getOriginalUrlAsString()) ;
      if(context == null) {
        logger.warn("SiteConfig is not found for the url ", urlDatum.getOriginalUrlAsString()) ;
        continue ;
      }
      SiteContext siteConfigContext = context.getSiteContext() ;
      if(writer == null) {
        Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
        writer = segment.getWriter() ;
      }
      if(urlDatum.getStatus() != URLDatum.STATUS_NEW) {
        fetcherPlugins.postFetch(context, urlDatum, System.currentTimeMillis()) ;
        SiteScheduleStat scheduleStat = siteConfigContext.getAttribute(SiteScheduleStat.class) ;
        scheduleStat.addProcessCount(1) ;
      } else {
      }
      writer.append(urlDatum.getId(), urlDatum) ;
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