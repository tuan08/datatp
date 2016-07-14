package net.datatp.webcrawler.master;

import net.datatp.webcrawler.ResponseCode;
import net.datatp.webcrawler.site.SiteContext;
import net.datatp.webcrawler.site.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLFetchStateManagerPlugin implements URLFetchSchedulerPlugin {
  
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
    SiteContext scontext  = context.getSiteContext() ;
    //CONTROL REFRESH PERIOD
    long refreshPeriod = scontext.getSiteConfig().getRefreshPeriod() * 1000l;
    if(datum.getPageType() == URLDatum.PAGE_TYPE_LIST) {
      setPageListRefreshPeriod(datum, atTime, refreshPeriod) ;
    } else {
      setPageDetailRefreshPeriod(datum, atTime, refreshPeriod) ;
    }
  }

  private void setPageDetailRefreshPeriod(URLDatum datum, long atTime, long refreshPeriod) {
    if(datum.getLastResponseCode() != ResponseCode.OK) {
      datum.setNextFetchTime(atTime + (1 * 24 * 60 * 60 * 1000l)) ;
    } else {
      datum.setNextFetchTime(atTime + (45 * 24 * 60 * 60 * 1000l)) ;
    }
  }

  private void setPageListRefreshPeriod(URLDatum datum, long atTime, long refreshPeriod) {
    if(refreshPeriod <= 0) {
      if(datum.getDeep() <= 2) {
        refreshPeriod = 24 * 60 * 60 * 1000l ;
      } else {
        refreshPeriod = (datum.getDeep() * 2)*(24 * 60 * 60 * 1000l) ;
      }
    } 
    datum.setNextFetchTime(atTime + refreshPeriod) ;
  }
}