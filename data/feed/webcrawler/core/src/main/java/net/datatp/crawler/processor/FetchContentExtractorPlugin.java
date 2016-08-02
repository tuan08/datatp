package net.datatp.crawler.processor;

import java.util.List;

import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.SiteExtractor;
import net.datatp.xhtml.extract.WDataExtract;
import net.datatp.xhtml.extract.WDataExtractContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchContentExtractorPlugin implements FetchProcessorPlugin {
  @Override
  public void process(FetchContext fetchCtx, WDataExtractContext wDataCtx) {
    SiteContext siteContext = fetchCtx.getURLContext().getSiteContext();
    SiteExtractor siteExtractor = siteContext.getSiteExtractor();
    List<WDataExtract> extracts = siteExtractor.extract(wDataCtx);
    if(extracts == null) return;
    for(WDataExtract sel : extracts) {
      
    }
  }
}