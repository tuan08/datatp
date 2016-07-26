package net.datatp.crawler.scheduler;

import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public interface URLSchedulerPlugin {
  public void preFetch(URLContext context,  URLDatum urldatum, long atTime) ;
  public void postFetch(URLContext context, URLDatum urldatum, long atTime) ;
}
