package net.datatp.crawler.site;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.URLInfo;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * May 4, 2010  
 */
public class URLContext {
  private URLDatum    urlDatum;
  private URLInfo   urlParser;
  private SiteContext siteContext;
  
  public URLContext(URLDatum urlDatum, URLInfo urlParser, SiteContext siteContext) {
    this.urlDatum    = urlDatum;
    this.urlParser   = urlParser;
    this.siteContext = siteContext;
  }
  
  public URLContext(URLInfo urlParser, SiteContext siteContext) {
    this.urlParser = urlParser ;
    this.siteContext = siteContext ;
  }
  
  public URLDatum getURLDatum() { return urlDatum; }
  
  public URLInfo getUrlParser() { return urlParser; }

  public SiteContext getSiteContext() { return siteContext ; }
}