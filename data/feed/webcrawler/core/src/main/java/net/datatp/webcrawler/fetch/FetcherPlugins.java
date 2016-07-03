package net.datatp.webcrawler.fetch;

import java.util.List;
import org.springframework.stereotype.Component;

import net.datatp.webcrawler.urldb.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
@Component
public class FetcherPlugins {
  private FetcherPlugin[] plugins = {new URLDatumStateManagerPlugin() } ;
  
  public void setPlugins(List<FetcherPlugin> plugins) {
    this.plugins = new FetcherPlugin[plugins.size()] ;
    for(int i = 0; i < this.plugins.length; i++) {
      this.plugins[i] = plugins.get(i) ;
    }
  }
  
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