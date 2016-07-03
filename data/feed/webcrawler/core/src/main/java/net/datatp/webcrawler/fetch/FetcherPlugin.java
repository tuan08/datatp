package net.datatp.webcrawler.fetch;

import net.datatp.webcrawler.urldb.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
public interface FetcherPlugin {
  public void preFetch(URLContext context,  URLDatum urldatum, long atTime) ;
  public void postFetch(URLContext context, URLDatum urldatum, long atTime) ;
}
