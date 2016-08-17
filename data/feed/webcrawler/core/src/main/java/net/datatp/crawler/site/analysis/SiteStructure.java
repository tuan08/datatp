package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLPattern;
import net.datatp.util.URLParser;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructure {
  private URLStructure urlStructure = new URLStructure();
  
  public URLStructure getUrlStructure() { return urlStructure; }
  
  public void analyse(SiteContext siteContext, WDataExtractContext ctx) {
    URLParser urlParser = ctx.getURLParser();
    URLPattern urlPattern = siteContext.matchesURLPattern(urlParser);
    if(urlPattern != null) {
      urlParser.addTag("url:type:" + urlPattern.getType());
    }
    urlStructure.add(urlParser);
    System.out.println("SiteStructureAnalyzer: Fetch" + urlParser.getNormalizeURLAll());
  }
}
