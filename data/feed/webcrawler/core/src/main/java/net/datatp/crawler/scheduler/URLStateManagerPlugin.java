package net.datatp.crawler.scheduler;

import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLStateManagerPlugin implements URLSchedulerPlugin {
  
  public void preFetch(URLContext context, URLDatum datum, long atTime) {
    datum.setStatus(URLDatum.STATUS_FETCHING) ;
    if(datum.getLastFetchFinishAt() <= 0) {
      datum.setLastFetchWaitingPeriod(0L) ;
    } else {
      datum.setLastFetchWaitingPeriod(atTime - datum.getLastFetchFinishAt()) ;
    }
    datum.setLastFetchScheduleAt(atTime) ;
    
    if(datum.getDeep() > 3) {
      datum.setNextFetchTime(atTime + (24 * 60 * 60 * 1000l)) ;
    } else {
      datum.setNextFetchTime(atTime + (6 * 60 * 60 * 1000l)) ;
    }
  }

  public void postFetch(URLContext context, URLDatum datum, long atTime) {
    datum.setStatus(URLDatum.STATUS_WAITING) ;
    datum.setLastFetchFinishAt(atTime) ;
    datum.setFetchCount(datum.getFetchCount() + 1) ;
  }
}