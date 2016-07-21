package net.datatp.http.crawler.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 19, 2010  
 */
public class SiteContextManager {
  private Map<String, SiteContext> siteContexts = new HashMap<String, SiteContext>();

  public void addCongfig(String site, String injectUrl, int crawlDeep) {
    addConfig(new SiteConfig("default", site, injectUrl, crawlDeep));
  }

  public void addConfig(SiteConfig config) {
    siteContexts.put(config.getHostname(), new SiteContext(config)) ;
  }
  
  public void addConfig(List<SiteConfig> configs) {
    for(int i = 0; i < configs.size(); i++) {
      SiteConfig config = configs.get(i);
      siteContexts.put(config.getHostname(), new SiteContext(config)) ;
    }
  }

  public int clear() { 
    int size = siteContexts.size() ;
    siteContexts.clear() ;
    return size  ;
  }

  public Map<String, SiteContext> getSiteConfigContexts() { 
    return this.siteContexts ; 
  }

  public List<SiteContext> getSiteConfigContextList() {
    List<SiteContext> holder = new ArrayList<SiteContext>() ;
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) holder.add(i.next()) ;
    return holder ;
  }

  public List<SiteConfig> getSiteConfigs() {
    List<SiteConfig> holder = new ArrayList<SiteConfig>() ;
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) holder.add(i.next().getSiteConfig()) ;
    return holder ;
  }

  public int getMaxProcess() { 
    int ret = getSiteConfigContexts().size() * 2 ; 
    if(ret < 500) return 500 ;
    return ret ;
  }

  public int getInQueueCount() { 
    int count = 0 ;
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) {
      SiteContext context = i.next() ;
      SiteScheduleStat stat = context.getAttribute(SiteScheduleStat.class) ;
      count += stat.getScheduleCount() - stat.getProcessCount() ;
    }
    return count ;
  }

  public SiteContext getSiteContext(String url)  {
    URLParser urlnorm = new URLParser(url) ;
    return getSiteConfigContext(urlnorm) ;
  }

  public SiteContext getSiteConfigContext(URLParser urlnorm)  {
    String hostname = urlnorm.getNormalizeHostName() ;
    if(hostname.startsWith("mail.") || hostname.startsWith("webmail.") || hostname.startsWith("email")) return null; 
    String[] source = urlnorm.getSources() ;
    for(String sel : source) {
      if(sel.startsWith("www")) continue ;
      SiteContext context = siteContexts.get(sel) ;
      if(context == null) continue ;
      if(context.allowURL(urlnorm)) return context ; 
    }
    return null;
  }

  public URLContext getURLContext(String url)  {
    URLParser urlnorm = new  URLParser(url) ;
    SiteContext context = getSiteConfigContext(urlnorm) ;
    if(context != null) return new URLContext(urlnorm, context) ;
    return null;
  }

  public void onPostPreSchedule() {
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) {
      SiteContext context = i.next() ;
      //context.getURLDatumStatistic().onPostPreSchedule() ;
      context.getAttribute(URLStatistics.class).onPostPreSchedule(); 
    }
  }

  final static public String[] getURLPaths(String url) {
    if(url.endsWith("/")) url = url.substring(0, url.length() - 1) ;
    List<String> holder = new ArrayList<String>() ;
    int idx = url.indexOf("//") ;
    if(idx < 0) {
      throw new RuntimeException("url should start with protocol://, " + url) ;
    }
    idx += 2 ;
    while(idx > 0) {
      idx = url.indexOf("/", idx + 1) ;
      if(idx > 0) {
        holder.add(url.substring(0, idx)) ;
      }
    }
    idx = url.indexOf('?') ;
    if(idx > 0) {
      holder.add(url.substring(0, idx)) ;
    } else {
      holder.add(url) ;
    }
    String[] array = new String[holder.size()] ;
    for(int i = 0; i < holder.size(); i++) {
      array[i] = holder.get(holder.size() - i - 1) ;
    }
    return array ;
  }
}