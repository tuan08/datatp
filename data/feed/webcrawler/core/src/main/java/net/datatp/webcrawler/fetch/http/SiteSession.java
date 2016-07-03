package net.datatp.webcrawler.fetch.http;

import net.datatp.webcrawler.fetch.FetchData;
import net.datatp.webcrawler.urldb.URLContext;

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
