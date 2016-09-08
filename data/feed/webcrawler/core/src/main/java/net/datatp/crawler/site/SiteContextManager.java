package net.datatp.crawler.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.URLInfo;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 19, 2010  
 */
public class SiteContextManager {
  private Map<String, SiteContext> siteContexts   = new HashMap<String, SiteContext>();
  private AutoWDataExtractors autoWDataExtractors = new AutoWDataExtractors();
  
  public void add(String site, String injectUrl, int crawlDeep) {
    add(new SiteConfig("default", site, injectUrl, crawlDeep));
  }

  public void add(SiteConfig config) {
    siteContexts.put(config.getHostname(), new SiteContext(config, autoWDataExtractors)) ;
  }
  
  public void add(List<SiteConfig> configs) {
    for(int i = 0; i < configs.size(); i++) {
      add(configs.get(i));
    }
  }


  public void update(SiteConfig config) {
    SiteContext siteContext = siteContexts.get(config.getHostname());
    siteContext.update(config);
  }
  
  public void update(List<SiteConfig> configs) {
    for(int i = 0; i < configs.size(); i++) {
      SiteConfig config = configs.get(i);
      SiteContext siteContext = siteContexts.get(config.getHostname());
      siteContext.update(config);
    }
  }
  
  public String[] remove(String group, String ... site) throws Exception {
    List<String> rmSites = new ArrayList<String>();
    for(String selSite : site) {
      SiteContext rmCtx = siteContexts.remove(selSite);
      if(rmCtx != null) rmSites.add(selSite);
    }
    return rmSites.toArray(new String[rmSites.size()]);
  }
  
  
  public int clear() { 
    int size = siteContexts.size() ;
    siteContexts.clear() ;
    return size  ;
  }

  public Map<String, SiteContext> getSiteConfigContexts() { return siteContexts ; }

  public List<SiteConfig> getSiteConfigs() {
    List<SiteConfig> holder = new ArrayList<SiteConfig>() ;
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) holder.add(i.next().getSiteConfig()) ;
    return holder ;
  }

  public int getInQueueCount() { 
    int count = 0 ;
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) {
      SiteContext context = i.next() ;
      SiteScheduleStat stat = context.getSiteScheduleStat() ;
      count += stat.getScheduleCount() - stat.getProcessCount() ;
    }
    return count ;
  }

  public SiteContext getSiteContext(String url)  { return getSiteConfigContext(new URLInfo(url)) ; }

  public SiteContext getSiteConfigContext(URLInfo urlParser)  {
    String hostname = urlParser.getNormalizeHostName() ;
    if(hostname.startsWith("mail.") || hostname.startsWith("webmail.") || hostname.startsWith("email")) return null; 
    String[] source = urlParser.getSources() ;
    for(String sel : source) {
      if(sel.startsWith("www")) continue ;
      SiteContext context = siteContexts.get(sel) ;
      if(context == null) continue ;
      if(context.allowDomain(urlParser)) return context ; 
    }
    return null;
  }

  public URLContext getURLContext(URLDatum urlDatum)  {
    URLInfo urlParser = new  URLInfo(urlDatum.getOriginalUrl()) ;
    SiteContext context = getSiteConfigContext(urlParser) ;
    if(context != null) return new URLContext(urlDatum, urlParser, context) ;
    return null;
  }

  public void onPostPreSchedule() {
    Iterator<SiteContext> i = siteContexts.values().iterator() ;
    while(i.hasNext()) {
      SiteContext context = i.next() ;
      context.getURLStatistics().onPostPreSchedule(); 
    }
  }
}