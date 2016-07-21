package net.datatp.http.crawler.fetcher;

import java.util.LinkedHashMap;
import java.util.Map;

import net.datatp.http.crawler.site.SiteContext;


/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 23, 2010  
 */
public class SiteSessionManager {
  private LinkedHashMap<String, SiteSessions> sessions ;

  public SiteSessionManager() {
    sessions = new LinkedHashMap<String, SiteSessions>(10000) {
      protected boolean removeEldestEntry(Map.Entry<String, SiteSessions> eldest) {
        if(size() == 10000) {
          eldest.getValue().destroy() ;
          return true ;
        }
        return false ;
      }
    } ;
  }

  public SiteSessions getSiteSession(SiteContext context) {
    String hostname = context.getSiteConfig().getHostname() ;
    SiteSessions siteSessions = sessions.get(hostname) ;
    if(siteSessions == null) {
      synchronized(this) {
        siteSessions = sessions.get(hostname) ;
        if(siteSessions == null) {
          siteSessions = createSiteSessions(hostname, context.getMaxConnection()) ;
          sessions.put(hostname, siteSessions) ;
        }
      }
    }
    return siteSessions;
  }

  public SiteSessions createSiteSessions(String hostname, int maxConnection) {
    return new SiteSessions(hostname, maxConnection) ;
  }
}