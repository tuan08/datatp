package net.datatp.webcrawler.urldb;

import net.datatp.util.URLParser;
import net.datatp.webcrawler.site.SiteContext;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
public class URLContext {
  private URLParser   urlParser;
  private SiteContext siteConfigContext;

  public URLContext(URLParser urlParser, SiteContext siteConfigContext) {
    this.urlParser = urlParser ;
    this.siteConfigContext = siteConfigContext ;
  }

  public URLParser getUrlNormalizer() { return urlParser; }
  public void setUrlNormalizer(URLParser urlNormalizer) { this.urlParser = urlNormalizer; }

  public SiteContext getSiteContext() { return siteConfigContext ; }
  public void setSiteConfig(SiteContext siteConfigContext) { 
  	this.siteConfigContext = siteConfigContext; 
  }
}