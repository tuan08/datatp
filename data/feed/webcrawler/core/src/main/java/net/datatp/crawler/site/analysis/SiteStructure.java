package net.datatp.crawler.site.analysis;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLPattern;
import net.datatp.util.URLAnalyzer;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructure {
  private URLSiteStructure urlStructure = new URLSiteStructure();
  private Map<String, WDataExtractContext> wdataContexts = new HashMap<>();
  
  public URLSiteStructure getUrlStructure() { return urlStructure; }
  
  public void analyse(SiteContext siteContext, WDataExtractContext ctx) {
    URLAnalyzer urlAnalyzer = ctx.getURLAnalyzer();
    URLPattern urlPattern = siteContext.matchesURLPattern(urlAnalyzer);
    if(urlPattern != null) {
      urlAnalyzer.addTag("url:type:" + urlPattern.getType());
    }
    urlStructure.add(urlAnalyzer);
    ctx.reset();
    wdataContexts.put(urlAnalyzer.getUrl(), ctx);
  }
  
  public URLStructure getURLStructure(String url) {
    WDataExtractContext ctx = wdataContexts.get(url);
    if(ctx != null) {
      return new URLStructure(ctx.getURLAnalyzer(), getXhtmlContent(ctx));
    }
    return new URLStructure(new URLAnalyzer(url), "No Data");
  }
  
  public WDataExtractContext getWDataExtractContext(String url) {
    return wdataContexts.get(url);
  }
  
  public String getXhtmlContent(WDataExtractContext ctx) {
    URLAnalyzer urlAnalyzer = ctx.getURLAnalyzer();
    Document doc = ctx.createDocument() ;
//    Element baseEle = doc.select("html > head > base").first();
//    if(baseEle == null) {
//      baseEle = new Element(Tag.valueOf("base"), "");
//      doc.select("html > head").first().appendChild(baseEle);
//    }
//    baseEle.attr("href", urlAnalyzer.getSiteURL());
    return doc.html();
  }
}
