package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.SiteConfig;

public class SiteStructureAnalyzerConfig {
  private SiteConfig siteConfig;
  private int        maxDownload = 100;
  private boolean    forceNew;
  
  public SiteStructureAnalyzerConfig()  {}
  
  public SiteStructureAnalyzerConfig(SiteConfig siteConfig, int maxDownload) {
    this.siteConfig  = siteConfig;
    this.maxDownload = maxDownload;
  }
  
  public SiteConfig getSiteConfig() { return siteConfig; }
  public void setSiteConfig(SiteConfig siteConfig) { this.siteConfig = siteConfig; }
  
  public int getMaxDownload() { return maxDownload; }
  public void setMaxDownload(int maxDownload) { this.maxDownload = maxDownload; }

  public boolean isForceNew() { return forceNew; }
  public void setForceNew(boolean forceNew) { this.forceNew = forceNew; }
}
