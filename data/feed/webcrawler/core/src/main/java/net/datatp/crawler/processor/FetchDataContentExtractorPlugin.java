package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.site.SiteContext;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataContentExtractorPlugin implements FetchDataProcessorPlugin {
  
  @Override
  public void process(FetchData fdata, URLContext urlContext, WData wPageData, XPathStructure structure) {
    SiteContext siteContext = urlContext.getSiteContext();
  }
}