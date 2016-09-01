package net.datatp.crawler.site;

import net.datatp.util.URLInfo;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class SiteContext {
  
  private SiteConfig          siteConfig;
  private WebPageTypeAnalyzer webPageTypeAnalyzer;
  private SiteScheduleStat    siteScheduleStat = new SiteScheduleStat();
  private URLStatistics       urlStatistics    = new URLStatistics();
  
  private SiteExtractor       siteExtractor;

  public SiteContext(SiteConfig siteConfig, AutoWDataExtractors autoWDataExtractors) {
    update(siteConfig);
    this.siteExtractor = new SiteExtractor(siteConfig, autoWDataExtractors);
  }
  
  public void update(SiteConfig siteConfig) {
    this.siteConfig    = siteConfig ;
    this.webPageTypeAnalyzer = new WebPageTypeAnalyzer(siteConfig.getWebPageTypePatterns());
    if(siteExtractor != null) siteExtractor.update(siteConfig);
  }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  
  public SiteScheduleStat getSiteScheduleStat() { return siteScheduleStat; }
  
  public URLStatistics getURLStatistics() { return urlStatistics ; }
  
  public WebPageTypeAnalyzer getWebPageTypeAnalyzer() { return webPageTypeAnalyzer; }
  
  public SiteExtractor getSiteExtractor() { return siteExtractor; }
  
  public int getMaxConnection() { 
    int max = siteConfig.getMaxConnection() ;
    if(max < 1) max = 1 ;
    return max ;
  }
  
  public int getMaxSchedule() {
    return siteScheduleStat.getMaxSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
  }
  
  public boolean canSchedule() {
    return siteScheduleStat.canSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
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