package net.datatp.crawler.fetcher;

import net.datatp.util.AllocatorAgent;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 8, 2010
 */
public class SiteSessions {
  final static int MAX_CONNECTION = 1 ;
  
  private AllocatorAgent<SiteSession> allocator ;
  
  public SiteSessions(String hostname, int maxConnection) {
    SiteSession[] sessions = new SiteSession[maxConnection] ;
    for(int i = 0; i < sessions.length; i++) {
      sessions[i] = new SiteSession(hostname) ;
    }
    allocator = new AllocatorAgent.RoundRobin<>(sessions) ;
  }
  
  synchronized public SiteSession next() { return allocator.next() ; }
 
  public SiteSession[] getSiteSessions() { return this.allocator.getItems() ; }
  
  public void destroy() {
  }
}
