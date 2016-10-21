package net.datatp.crawler.site;

import net.datatp.crawler.site.analysis.WebPageAnalyzer;
import net.datatp.util.URLInfo;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class SiteContext {
  
  private SiteConfig          siteConfig;
  private SiteStatistic       siteStatistic ;

  private WebPageAnalyzer     webpageAnalyzer;

  public SiteContext(SiteConfig siteConfig, AutoWDataExtractors autoWDataExtractors) {
    this.webpageAnalyzer = new WebPageAnalyzer(autoWDataExtractors);
    update(siteConfig);
  }
  
  public void update(SiteConfig siteConfig) {
    this.siteConfig    = siteConfig ;
    this.siteStatistic = new SiteStatistic(siteConfig.getGroup(), siteConfig.getHostname());
    this.webpageAnalyzer.update(siteConfig);
  }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  
  public SiteStatistic getSiteStatistic() { return siteStatistic ; }
  
  public WebPageAnalyzer getWebPageAnalyzer() { return webpageAnalyzer; }
  
  public int getMaxConnection() { 
    int max = siteConfig.getMaxConnection() ;
    if(max < 1) max = 1 ;
    return max ;
  }
  
  public int getMaxSchedule() {
    return siteStatistic.getMaxSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
  }
  
  public boolean canSchedule() {
    return siteStatistic.canSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
  }

  public boolean allowDomain(URLInfo urlParser) {
    String hostname = urlParser.getNormalizeHostName() ;
    if(hostname.equals(siteConfig.getHostname())) return true ;
    if(siteConfig.getCrawlSubDomain()) {
      return hostname.endsWith(siteConfig.getHostname()) ;
    } 
    return false ;
  }
  
  public void update(SiteContext other) { this.siteConfig = other.siteConfig ; }
}