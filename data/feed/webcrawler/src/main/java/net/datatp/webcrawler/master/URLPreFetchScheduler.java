package net.datatp.webcrawler.master;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.channel.ChannelGateway;
import net.datatp.http.crawler.URLDatum;
import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.MergeMultiSegmentIterator;
import net.datatp.storage.kvdb.Segment;
import net.datatp.webcrawler.master.model.URLScheduleInfo;
import net.datatp.webcrawler.site.SiteContext;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.site.SiteScheduleStat;
import net.datatp.webcrawler.site.URLContext;
import net.datatp.webcrawler.urldb.URLDatumDB;
import net.datatp.webcrawler.urldb.URLDatumRecord;
import net.datatp.webcrawler.urldb.URLStatisticMap;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
@ManagedResource(
  objectName="net.datatp.webcrawler.fetcher:name=URLPreFetchScheduler", 
  description="This bean is responsible to schedule the urls"
)
@Component
public class URLPreFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPreFetchScheduler.class);

  @Autowired
  protected URLDatumDB urlDatumDB ;

  @Autowired
  private SiteContextManager siteContextManager ;

  @Autowired
  @Qualifier("URLFetchGateway")
  private ChannelGateway  urldatumFetchGateway ;

  @Autowired
  protected URLFetchSchedulerPlugins fetcherPlugins ;
  private URLFetchSchedulerVerifier  verifier = new URLFetchSchedulerVerifier () ;
  
  @Value("${crawler.master.scheduler.prefetch.max-per-site}")
  private int maxSchedulePerSite = 50 ;
  private int scheduleCounter = 0;
  
  private URLScheduleInfo lastScheduleInfo = null;
  
  @PostConstruct
  public void onInit() {
  }

  @ManagedAttribute(
    description = "Maximum frequency of url can schedule per site. " + 
                  "The real max frequency is the frequency of connection multiply by this frequency"
  )
  public int  getMaxSchedulePerSite() { return this.maxSchedulePerSite ; }
  @ManagedAttribute(
    description="set the maximum frequency of url can schedule per site.",
    defaultValue="5", currencyTimeLimit=20
  )
  public void setMaxSchedulePerSite(int max) { maxSchedulePerSite = max ; }

  @ManagedAttribute(description="The frequency of time this bean is invoked")
  public int getScheduleCounter() { return this.scheduleCounter ; }

  public URLScheduleInfo schedule() throws Exception {
    logger.info("Start scheduling the fetch request!") ;
    scheduleCounter += 1 ;
    
    MergeMultiSegmentIterator<Text, URLDatumRecord> mitr = urlDatumDB.getMergeRecordIterator() ;
    SortKeyValueFile<Text, URLDatumRecord>.Writer writer = null ;
    long currentTime = System.currentTimeMillis() ;
    int urlCount = 0;
    int errorCount = 0, delayScheduleCount = 0 ;
    int pendingCount = 0, expiredPendingCount = 0, waitingCount = 0, scheduleCount = 0 ;
    MultiListHolder<URLDatum> requestBuffer = new MultiListHolder<URLDatum>(300000);
    PriorityURLDatumHolder priorityUrlHolder = null ;
    while(mitr.next()) {
      urlCount++ ;
      URLDatum datum = mitr.currentValue() ;
      //In case The url has been schedule to fetch 6 hours ago. It could happen when the queue has problem
      //or the fetcher has problem and url datum is not updated, shedule to refetch.
      URLContext urlContext = siteContextManager.getURLContext(datum.getOriginalUrlAsString()) ;
      if(urlContext == null) {
        errorCount++ ;
        logger.info("Scheduler: URLContext for " + datum.getOriginalUrlAsString() + " is null!") ;
        continue ;
      }
      
      SiteContext siteContext = urlContext.getSiteContext() ;
      siteContext.getAttribute(URLStatisticMap.class).log(datum) ;
      
      boolean doFetch = false ;

      if(datum.getErrorCount() >= 3) continue ;

      if(currentTime > datum.getNextFetchTime()) {
        doFetch = true ;
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) expiredPendingCount++ ;
      } else {
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) {
          pendingCount++ ;
        } else {
          waitingCount++ ;
        }
      }
      
      if(!doFetch) continue;
      SiteScheduleStat scheduleStat = siteContext.getAttribute(SiteScheduleStat.class) ;
      if(!scheduleStat.canSchedule(maxSchedulePerSite, siteContext)) {
        delayScheduleCount++ ;
        continue ;
      }

      if(priorityUrlHolder == null) {
        priorityUrlHolder = 
            new PriorityURLDatumHolder(siteContext, scheduleStat.getMaxSchedule(maxSchedulePerSite, siteContext), 3) ;
      } else if(priorityUrlHolder.getSiteConfigContext() != siteContext) {
        if(requestBuffer.getCurrentSize() + priorityUrlHolder.getSize() > requestBuffer.getCapacity()) {
          if(writer == null) {
            Segment<Text, URLDatumRecord> segment = urlDatumDB.newSegment() ;
            writer = segment.getWriter() ;
          }
          scheduleCount += flush(requestBuffer, writer) ;
        }
        flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
        delayScheduleCount += priorityUrlHolder.getDelayCount() ;
        priorityUrlHolder = 
          new PriorityURLDatumHolder(siteContext, scheduleStat.getMaxSchedule(maxSchedulePerSite, siteContext), 3) ;
      } 
      priorityUrlHolder.insert(datum) ;
    }

    flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
    if(priorityUrlHolder != null) delayScheduleCount += priorityUrlHolder.getDelayCount() ;
    if(requestBuffer.getCurrentSize() > 0) {
      if(writer == null) {
        Segment<Text, URLDatumRecord> segment = urlDatumDB.newSegment() ;
        writer = segment.getWriter() ;
      }
      scheduleCount += flush(requestBuffer, writer) ;
    }
    if(writer != null) {
      writer.close() ;
      //urlDatumDB.autoCompact() ;
    }
    siteContextManager.onPostPreSchedule() ;
    logger.info(
      "Check {} urls, error {}, fetch pending {}, expired fetch pending {}, fetch waiting {}, schedule {}, delay schedule {}", 
      new Object[] {urlCount, errorCount, pendingCount, expiredPendingCount, waitingCount, scheduleCount, delayScheduleCount} 
    );
    int checkCount = errorCount + pendingCount + waitingCount + scheduleCount + delayScheduleCount ;
    if(urlCount != checkCount) {
      logger.warn("The frequency of check url is " + checkCount + ", but frequency of url in the db is " + urlCount) ;
    }
    verifier.verify(logger, urlCount, waitingCount) ;

    long execTime = System.currentTimeMillis() - currentTime ;
    URLScheduleInfo info = 
        new URLScheduleInfo(currentTime, execTime, urlCount, scheduleCount, delayScheduleCount, pendingCount, waitingCount) ;
    if(info.isChangedCompareTo(lastScheduleInfo)) {
      lastScheduleInfo = info;
      System.err.println("return schedule info!");
      return info;
    } else {
      lastScheduleInfo = info;
      return null;
    }
  }

  private void flushPriorityURLDatumHolder(PriorityURLDatumHolder holder, MultiListHolder<URLDatum> fRequestBuffer) throws Exception {
    if(holder == null) return ;
    for(URLDatum sel : holder.getURLDatum()) {
      URLContext selUrlContext = siteContextManager.getURLContext(sel.getOriginalUrlAsString()) ;
      fetcherPlugins.preFetch(selUrlContext, sel, System.currentTimeMillis()) ;
      fRequestBuffer.add(selUrlContext.getUrlNormalizer().getHost(), sel) ;
      SiteScheduleStat scheduleStat = selUrlContext.getSiteContext().getAttribute(SiteScheduleStat.class) ;
      scheduleStat.addScheduleCount(1) ;
    }
  }

  private int flush(MultiListHolder<URLDatum> urlDatumBuffer, SortKeyValueFile<Text, URLDatumRecord>.Writer writer) throws Exception {
    MultiListHolder<URLDatum>.RandomIterator iterator = urlDatumBuffer.getRandomIterator() ;
    URLDatumRecord datum = null ;
    int scheduleCount = 0 ;
    ArrayList<URLDatumRecord> holder = new ArrayList<>(100) ;
    while((datum = (URLDatumRecord)iterator.next()) != null) {
      writer.append(new Text(datum.getId()), datum) ;
      holder.add(datum) ;
      if(holder.size() == 100) {
        urldatumFetchGateway.send(holder);
        holder.clear() ;
      }
      scheduleCount++ ;
    }
    if(holder.size() > 0) {
      urldatumFetchGateway.send(holder) ;
      holder.clear() ;
    }
    urlDatumBuffer.assertEmpty() ;
    return scheduleCount ;
  }
}