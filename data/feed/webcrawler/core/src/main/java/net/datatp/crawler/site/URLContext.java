package net.datatp.crawler.site;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLContext {
  private URLDatum    urlDatum;
  private URLParser   urlParser;
  private SiteContext siteContext;
  
  public URLContext(URLDatum urlDatum, URLParser urlParser, SiteContext siteContext) {
    this.urlDatum    = urlDatum;
    this.urlParser   = urlParser;
    this.siteContext = siteContext;
  }
  
  public URLContext(URLParser urlParser, SiteContext siteContext) {
    this.urlParser = urlParser ;
    this.siteContext = siteContext ;
  }
  
  public URLDatum getURLDatum() { return urlDatum; }
  
  public URLParser getUrlParser() { return urlParser; }

  public SiteContext getSiteContext() { return siteContext ; }
}