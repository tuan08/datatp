package net.datatp.webcrawler.fetcher.http;

import net.datatp.webcrawler.fetcher.FetchData;
import net.datatp.webcrawler.site.URLContext;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 22, 2010  
 */
public interface SiteSession extends Comparable<SiteSession> {
  public String getHostname() ;
  public boolean isLocked() ;
  public void fetch(FetchData fdata, URLContext context) ;
  public void destroy()  ;
}
