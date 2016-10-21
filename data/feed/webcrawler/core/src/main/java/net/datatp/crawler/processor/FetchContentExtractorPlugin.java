package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.crawler.site.WebPageType;
import net.datatp.crawler.site.analysis.WebPageAnalysis;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchContentExtractorPlugin implements FetchProcessorPlugin {
  @Override
  public void process(FetchContext fetchCtx, WDataContext wDataCtx) {
    if(wDataCtx == null) return;
    
    WebPageAnalysis wpAnalysis = fetchCtx.getURLContext().getSiteContext().getWebPageAnalyzer().analyze(wDataCtx);
    for(ExtractEntity entity : wpAnalysis.getEntities()) {
      fetchCtx.getXDocMapper().addEntity(entity);
    }
    
    URLDatum urlDatum = fetchCtx.getURLContext().getURLDatum();
    WebPageType wpType = wpAnalysis.getWebPageType();
    if(wpType == WebPageType.list) {
      urlDatum.setPageType(URLDatum.PAGE_TYPE_LIST);
    } else if (wpType == WebPageType.detail) {
      urlDatum.setPageType(URLDatum.PAGE_TYPE_DETAIL);
    } else {
      urlDatum.setPageType(URLDatum.PAGE_TYPE_UNCATEGORIZED);
    }
    fetchCtx.getXDocMapper().setPageType(urlDatum.pageType());
  }
}