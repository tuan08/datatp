package net.datatp.crawler.scheduler;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumDB;
import net.datatp.crawler.urldb.URLDatumDBIterator;
import net.datatp.crawler.urldb.URLDatumDBWriter;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
abstract public class URLPreFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPreFetchScheduler.class);

  protected URLDatumDB         urlDatumDB;
  protected SiteContextManager siteContextManager ;

  protected URLSchedulerPluginManager schedulerPluginManager ;
  
  protected URLFetchSchedulerVerifier  verifier = new URLFetchSchedulerVerifier () ;
  
  protected int maxSchedulePerSite = 100 ;

  protected int scheduleCounter    = 0;
  
  private URLScheduleMetric lastScheduleMetric = null;

  
  abstract protected void onSchedule(ArrayList<URLDatum> holder) throws Exception ;
  
  public URLScheduleMetric schedule() throws Exception {
    logger.info("Start scheduling the fetch request!") ;
    scheduleCounter += 1 ;
    
    URLDatumDBIterator urlDatumDBItr = urlDatumDB.createURLDatumDBIterator();
    URLDatumDBWriter   writer = null ;
    
    long currentTime = System.currentTimeMillis();
    URLScheduleMetric scheduleMetric = new URLScheduleMetric();
    scheduleMetric.setTime(currentTime);
    MultiListHolder<URLDatum> requestBuffer = new MultiListHolder<URLDatum>(300000);
    PriorityURLDatumHolder priorityUrlHolder = null ;
    while(urlDatumDBItr.hasNext()) {
      URLDatum datum = urlDatumDBItr.next() ;
      scheduleMetric.log(datum);
      //In case The url has been schedule to fetch 6 hours ago. It could happen when the queue has problem
      //or the fetcher has problem and url datum is not updated, shedule to refetch.
      URLContext urlContext = siteContextManager.getURLContext(datum) ;
      if(urlContext == null) {
        scheduleMetric.logError(1);
        logger.info("Scheduler: URLContext for " + datum.getOriginalUrl() + " is null!") ;
        continue ;
      }
      
      SiteContext siteContext = urlContext.getSiteContext() ;
      siteContext.getURLStatistics().log(datum) ;
      
      boolean doFetch = false ;

      if(datum.getErrorCount() >= 3) continue ;

      if(currentTime > datum.getNextFetchTime()) {
        doFetch = true ;
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) {
          scheduleMetric.logExpiredPending(1); ;
        }
      } else {
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) {
          scheduleMetric.logPending(1);
        } else {
          scheduleMetric.logWaiting(1);
        }
      }
      
      if(!doFetch) continue;
      if(!siteContext.canSchedule()) {
        scheduleMetric.logDelay(1);
        continue ;
      }

      if(priorityUrlHolder == null) {
        priorityUrlHolder = new PriorityURLDatumHolder(siteContext, siteContext.getMaxSchedule(), 3) ;
      } else if(priorityUrlHolder.getSiteConfigContext() != siteContext) {
        if(requestBuffer.getCurrentSize() + priorityUrlHolder.getSize() > requestBuffer.getCapacity()) {
          if(writer == null) writer = urlDatumDB.createURLDatumDBWriter() ;
          int count = flush(requestBuffer, writer) ;
          scheduleMetric.logSchedule(count);
        }
        flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
        int delayCount = priorityUrlHolder.getDelayCount() ;
        scheduleMetric.logDelay(delayCount);
        priorityUrlHolder = new PriorityURLDatumHolder(siteContext, siteContext.getMaxSchedule(), 3) ;
      } 
      priorityUrlHolder.insert(datum) ;
    }
    
    flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
    if(priorityUrlHolder != null) {
      int delayCount = priorityUrlHolder.getDelayCount() ;
      scheduleMetric.logDelay(delayCount);
    }
    if(requestBuffer.getCurrentSize() > 0) {
      if(writer == null) writer = urlDatumDB.createURLDatumDBWriter();
      int scheduleCount = flush(requestBuffer, writer) ;
      scheduleMetric.logSchedule(scheduleCount);
    }
    
    urlDatumDBItr.close();
    if(writer != null) {
      writer.close() ;
      //urlDatumDB.autoCompact() ;
    }
    siteContextManager.onPostPreSchedule() ;
    logger.info(
      "Check {} urls, error {}, fetch pending {}, expired fetch pending {}, fetch waiting {}, schedule {}, delay schedule {}", 
      new Object[] {
          scheduleMetric.getUrlCount(), scheduleMetric.getErrorCount(), scheduleMetric.getPendingCount(), 
          scheduleMetric.getExpiredPendingCount(), scheduleMetric.getWaitingCount(), scheduleMetric.getScheduleCount(),
          scheduleMetric.getDelayScheduleCount() } 
    );
    long checkCount = 
        scheduleMetric.getErrorCount() + scheduleMetric.getPendingCount() + scheduleMetric.getWaitingCount() + 
        scheduleMetric.getScheduleCount() + scheduleMetric.getDelayScheduleCount() ;
    if(scheduleMetric.getUrlCount() != checkCount) {
      logger.warn("The frequency of check url is " + checkCount + ", but frequency of url in the db is " + scheduleMetric.getUrlCount()) ;
    }
    verifier.verify(logger, scheduleMetric.getUrlCount(), scheduleMetric.getWaitingCount()) ;

    scheduleMetric.setExecTime(System.currentTimeMillis() - currentTime);
    if(scheduleMetric.isChangedCompareTo(lastScheduleMetric)) {
      lastScheduleMetric = scheduleMetric;
      return scheduleMetric;
    }
    lastScheduleMetric = scheduleMetric;
    return null;
  }

  private void flushPriorityURLDatumHolder(PriorityURLDatumHolder holder, MultiListHolder<URLDatum> fRequestBuffer) throws Exception {
    if(holder == null) return ;
    for(URLDatum sel : holder.getURLDatum()) {
      URLContext urlContext = siteContextManager.getURLContext(sel) ;
      schedulerPluginManager.preFetch(urlContext, sel, System.currentTimeMillis()) ;
      fRequestBuffer.add(urlContext.getUrlParser().getHost(), sel) ;
      urlContext.getSiteContext().getSiteScheduleStat().addProcessCount(1); ;
    }
  }

  private int flush(MultiListHolder<URLDatum> urlDatumBuffer, URLDatumDBWriter writer) throws Exception {
    MultiListHolder<URLDatum>.RandomIterator iterator = urlDatumBuffer.getRandomIterator() ;
    URLDatum datum = null ;
    int scheduleCount = 0 ;
    ArrayList<URLDatum> holder = new ArrayList<>(100) ;
    while((datum = iterator.next()) != null) {
      writer.write(datum) ;
      holder.add(datum) ;
      if(holder.size() == 100) {
        onSchedule(holder);
        holder.clear() ;
      }
      scheduleCount++ ;
    }
    if(holder.size() > 0) {
      onSchedule(holder) ;
      holder.clear() ;
    }
    urlDatumBuffer.assertEmpty() ;
    return scheduleCount ;
  }
}