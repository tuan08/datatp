package net.datatp.webcrawler.master;

import net.datatp.http.crawler.URLDatum;
import net.datatp.webcrawler.site.URLContext;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public interface URLFetchSchedulerPlugin {
  public void preFetch(URLContext context,  URLDatum urldatum, long atTime) ;
  public void postFetch(URLContext context, URLDatum urldatum, long atTime) ;
}
