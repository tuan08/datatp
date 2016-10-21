package net.datatp.crawler.scheduler;

import net.datatp.crawler.http.ResponseCode;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLNextFetchTimePlugin implements URLSchedulerPlugin {
  final static public long SIX_HOURS = 6 * 60 * 60 * 1000;
  final static public long ONE_DAY   = 24 * 60 * 60 * 1000;
  final static public long THREE_DAY = 3 * ONE_DAY;
  final static public long ONE_WEEK  = 7 * ONE_DAY;
  final static public long ONE_YEAR  = 365 * ONE_DAY;
  
  public void preFetch(URLContext context, URLDatum datum, long atTime) { }

  public void postFetch(URLContext context, URLDatum datum, long atTime) {
    SiteContext scontext  = context.getSiteContext() ;
    if(datum.getLastResponseCode() == ResponseCode.OK) {
      long refreshPeriod = scontext.getSiteConfig().getRefreshPeriod() * 1000;
      long lifetime = datum.getCreatedTime() - atTime;
      if(lifetime > THREE_DAY) {
        datum.setNextFetchTime(atTime + ONE_YEAR) ;
      } else {
        if(datum.getDeep() <= 2) {
          datum.setNextFetchTime(atTime + refreshPeriod) ;
        } else {
          datum.setNextFetchTime(atTime + THREE_DAY) ;
        }
      }
    } else {
      if(datum.getErrorCount() > 3) {
        //Too many error, ignore
        datum.setNextFetchTime(atTime + ONE_YEAR) ;
      } else {
        datum.setNextFetchTime(atTime + ONE_DAY) ;
      }
    }
  }
}