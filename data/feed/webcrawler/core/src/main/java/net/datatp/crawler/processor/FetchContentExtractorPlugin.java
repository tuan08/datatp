package net.datatp.crawler.processor;

import java.util.List;

import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.SiteExtractor;
import net.datatp.crawler.site.WebPageType;
import net.datatp.crawler.site.WebPageTypeAnalyzer;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataExtractContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchContentExtractorPlugin implements FetchProcessorPlugin {
  @Override
  public void process(FetchContext fetchCtx, WDataExtractContext wDataCtx) {
    if(wDataCtx == null) return;
    
    WebPageTypeAnalyzer wpAnalyzer = fetchCtx.getURLContext().getSiteContext().getWebPageTypeAnalyzer();
    WebPageType wpType = wpAnalyzer.analyze(wDataCtx.getWdata().getAnchorText(), wDataCtx.getWdata().getUrl());
    if(wpType == WebPageType.ignore || wpType == WebPageType.list) return;
    if(wpAnalyzer.hasDetailPatternConfig() && wpType == WebPageType.uncategorized) return;

    SiteContext siteContext      = fetchCtx.getURLContext().getSiteContext();
    SiteExtractor siteExtractor  = siteContext.getSiteExtractor();
    List<ExtractEntity> extracts = siteExtractor.extract(wDataCtx);
    
    if(extracts == null || extracts.size() == 0) {
      fetchCtx.getXDocMapper().setPageType("uncategorized");
      return;
    }
    
    for(ExtractEntity sel : extracts) {
      fetchCtx.getXDocMapper().addEntity(sel);
    }
    fetchCtx.getXDocMapper().setPageType("detail");
    URLDatum urlDatum = fetchCtx.getURLContext().getURLDatum();
    if(urlDatum.getPageType() != URLDatum.PAGE_TYPE_DETAIL) {
      urlDatum.setPageType(URLDatum.PAGE_TYPE_DETAIL);
    }
  }
}