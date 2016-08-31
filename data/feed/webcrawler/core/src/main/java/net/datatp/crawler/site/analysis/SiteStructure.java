package net.datatp.crawler.site.analysis;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLPattern;
import net.datatp.util.URLInfo;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructure {
  private URLSiteStructure                 urlSiteStructure = new URLSiteStructure();
  private SiteContext                      siteContext;
  private Map<String, WDataExtractContext> wdataContexts    = new HashMap<>();
  private int                              count            = 0;

  public SiteStructure(SiteContext siteContext) {
    this.siteContext = siteContext;
  }
  
  public URLSiteStructure getUrlSiteStructure() { return urlSiteStructure; }
  
  public void update(SiteContext siteContext) {
    this.siteContext = siteContext;
  }
  
  synchronized public void analyse(WDataExtractContext ctx) {
    URLAnalysis urlInfo = new URLAnalysis();
    urlInfo.setUrlInfo(ctx.getURLAnalyzer());
    URLPattern urlPattern = siteContext.matchesURLPattern(urlInfo.getUrlInfo());
    if(urlPattern != null) {
      urlInfo.setPageTypeCategory(urlPattern.getType().toString());
    }
    urlSiteStructure.add(urlInfo);
    ctx.reset();
    wdataContexts.put(urlInfo.getUrlInfo().getUrl(), ctx);
    count++;
    System.out.println(count + ". " + urlInfo.getUrlInfo().getUrl());
  }
  
  synchronized public void reanalyse() {
    System.out.println("Reanalyse SiteStructure");
    WDataExtractContext[] wdataExtractContexts = 
      wdataContexts.values().toArray(new WDataExtractContext[wdataContexts.size()]);
    urlSiteStructure.clear();
    wdataContexts.clear();
    count = 0;
    for(WDataExtractContext sel : wdataExtractContexts) {
      analyse(sel);
    }
  }
  
  public URLData getURLData(String url) {
    WDataExtractContext ctx = wdataContexts.get(url);
    if(ctx != null) {
      Document doc = ctx.createDocument() ;
      return new URLData(ctx.getURLAnalyzer(), doc.html());
    }
    return new URLData(new URLInfo(url), "No Data");
  }
}
