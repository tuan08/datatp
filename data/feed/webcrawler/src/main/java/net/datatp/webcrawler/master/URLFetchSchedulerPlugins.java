package net.datatp.webcrawler.master;

import org.springframework.stereotype.Component;

import net.datatp.http.crawler.URLDatum;
import net.datatp.webcrawler.site.URLContext;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
@Component
public class URLFetchSchedulerPlugins {
  private URLFetchSchedulerPlugin[] plugins = { new URLFetchStateManagerPlugin() } ;
  
  public void preFetch(URLContext context, URLDatum frequest, long atTime) {
    for(int i = 0; i < plugins.length; i++) {
      plugins[i].preFetch(context, frequest, atTime) ;
    }
  }
  
  public void postFetch(URLContext context, URLDatum data, long atTime) {
    for(int i = 0; i < plugins.length; i++) {
      plugins[i].postFetch(context, data, atTime) ;
    }
  }
}