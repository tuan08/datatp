package net.datatp.crawler.site;

import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLContext {
  private URLParser   urlParser;
  private SiteContext siteConfigContext;

  public URLContext(URLParser urlParser, SiteContext siteConfigContext) {
    this.urlParser = urlParser ;
    this.siteConfigContext = siteConfigContext ;
  }

  public URLParser getUrlParser() { return urlParser; }
  public void setUrlParser(URLParser urlParser) { this.urlParser = urlParser; }

  public SiteContext getSiteContext() { return siteConfigContext ; }
  public void setSiteConfig(SiteContext siteConfigContext) { 
  	this.siteConfigContext = siteConfigContext; 
  }
}