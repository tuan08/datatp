package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.xpath.WDataContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataContentExtractorPlugin implements FetchDataProcessorPlugin {
  @Override
  public void process(FetchData fdata, URLContext urlContext, WDataContext context) {
    SiteContext siteContext = urlContext.getSiteContext();
  }
}