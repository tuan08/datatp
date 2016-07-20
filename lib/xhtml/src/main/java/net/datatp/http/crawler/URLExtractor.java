package net.datatp.http.crawler;

import java.util.Map;
import java.util.regex.Pattern;

import net.datatp.xhtml.util.URLRewriter;
import net.datatp.xhtml.util.URLSessionIdCleaner;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com 
 *          Jun 23, 2010
 */
public class URLExtractor {
  final static URLSessionIdCleaner URL_CLEANER = new URLSessionIdCleaner() ;

  private URLRewriter  urlRewriter = new URLRewriter();
  private Pattern[]    excludePatternMatchers         ;
  
  public URLExtractor() {
  }

  public Map<String, String> extract(XPathStructure structure) {
    return null;
  }
}