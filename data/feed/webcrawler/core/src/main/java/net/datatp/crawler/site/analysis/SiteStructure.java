package net.datatp.crawler.site.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.WebPageType;
import net.datatp.crawler.site.WebPageTypeAnalyzer;
import net.datatp.util.URLInfo;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructure {
  private SiteContext                      siteContext;
  private URLSiteStructure                 urlSiteStructure = new URLSiteStructure();
  
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
    URLAnalysis urlAnalysis = new URLAnalysis();
    urlAnalysis.setUrlInfo(ctx.getURLAnalyzer());
    WebPageTypeAnalyzer wpAnalyzer = siteContext.getWebPageTypeAnalyzer();
    WebPageType wpType = wpAnalyzer.analyze(ctx.getWdata().getAnchorText(), urlAnalysis.getUrlInfo().getNormalizeURL());
    urlAnalysis.setPageType(wpType.toString());
    if(wpType == WebPageType.detail) {
      List<ExtractEntity> entities = siteContext.getSiteExtractor().extract(ctx);
      urlAnalysis.withExtractEntityInfo(entities);
    }
    urlSiteStructure.add(urlAnalysis);
    
    ctx.reset();
    wdataContexts.put(urlAnalysis.getUrlInfo().getUrl(), ctx);
    count++;
    System.out.println(count + ". " + urlAnalysis.getUrlInfo().getUrl());
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
