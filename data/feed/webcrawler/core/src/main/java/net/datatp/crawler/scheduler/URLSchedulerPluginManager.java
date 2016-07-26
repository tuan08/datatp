package net.datatp.crawler.scheduler;

import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLSchedulerPluginManager {
  private URLSchedulerPlugin[] plugins = { new URLStateManagerPlugin() } ;
  
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