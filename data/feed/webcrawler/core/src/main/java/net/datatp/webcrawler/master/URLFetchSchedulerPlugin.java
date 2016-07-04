package net.datatp.webcrawler.master;

import net.datatp.webcrawler.urldb.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public interface URLFetchSchedulerPlugin {
  public void preFetch(URLContext context,  URLDatum urldatum, long atTime) ;
  public void postFetch(URLContext context, URLDatum urldatum, long atTime) ;
}
