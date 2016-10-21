package net.datatp.crawler.site.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.WebPageType;
import net.datatp.util.URLInfo;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataContext;

public class SiteStructure {
  private SiteContext                      siteContext;
  private URLSiteStructure                 urlSiteStructure = new URLSiteStructure();
  private Map<String, WDataContext> wdataContexts    = new HashMap<>();

  public SiteStructure(SiteContext siteContext) {
    this.siteContext = siteContext;
  }

  public URLSiteStructure getUrlSiteStructure() { return urlSiteStructure; }

  public void update(SiteContext siteContext) {
    this.siteContext = siteContext;
  }

  synchronized public void analyse(WDataContext ctx) {
    URLAnalysis urlAnalysis = new URLAnalysis();
    urlAnalysis.setUrlInfo(ctx.getURInfo());
    WebPageAnalysis wpAnalysis = siteContext.getWebPageAnalyzer().analyze(ctx);
    WebPageType wpType = wpAnalysis.getWebPageType();
    urlAnalysis.setPageType(wpType.toString());
    urlAnalysis.withExtractEntityInfo(wpAnalysis.getEntities());
    urlSiteStructure.add(urlAnalysis);

    ctx.reset();
    wdataContexts.put(urlAnalysis.getUrlInfo().getUrl(), ctx);
  }

  synchronized public void reanalyse() {
    WDataContext[] wdataExtractContexts = 
        wdataContexts.values().toArray(new WDataContext[wdataContexts.size()]);
    urlSiteStructure.clear();
    wdataContexts.clear();
    for(WDataContext sel : wdataExtractContexts) {
      analyse(sel);
    }
  }

  public URLData getURLData(String url) {
    WDataContext ctx = wdataContexts.get(url);
    if(ctx != null) {
      Document doc = ctx.createDocument() ;
      return new URLData(ctx.getURInfo(), doc.html());
    }
    return new URLData(new URLInfo(url), "No Data");
  }

  List<ExtractEntity> extractEntities(WDataContext ctx) {
    WebPageAnalysis wpAnalysis = siteContext.getWebPageAnalyzer().analyze(ctx);
    return wpAnalysis.getEntities();
  }
}
