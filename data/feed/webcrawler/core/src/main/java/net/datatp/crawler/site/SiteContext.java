package net.datatp.crawler.site;

import java.io.Serializable;

import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class SiteContext implements Serializable {
  static public enum Modify { NONE, ADD, DELETE, MODIFIED }

  private Modify           modify           = Modify.NONE;
  private SiteConfig       siteConfig;
  private SiteScheduleStat siteScheduleStat = new SiteScheduleStat();
  private URLStatistics    urlStatistics    = new URLStatistics();
  private SiteExtractor    siteExtractor;
  

  public SiteContext(SiteConfig config, SiteExtractor siteExtractor) {
    this.siteConfig    = config ;
    this.siteExtractor = siteExtractor;
    
  }

  public Modify getModify() { return this.modify ; }
  public void setModify(Modify modify) { this.modify = modify ; }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  
  public SiteScheduleStat getSiteScheduleStat() { return siteScheduleStat; }
  
  public URLStatistics getURLStatistics() { return urlStatistics ; }
  
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

  public boolean allowURL(URLParser urlnorm) {
    String hostname = urlnorm.getNormalizeHostName() ;
    if(hostname.equals(siteConfig.getHostname())) return true ;
    if(siteConfig.getCrawlSubDomain()) {
      return hostname.endsWith(siteConfig.getHostname()) ;
    } 
    return false ;
  }

  public void update(SiteContext other) { this.siteConfig = other.siteConfig ; }
}