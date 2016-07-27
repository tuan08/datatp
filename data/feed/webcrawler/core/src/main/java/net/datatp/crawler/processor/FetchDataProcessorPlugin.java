package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.XPathStructure;

public interface FetchDataProcessorPlugin {
  public void process(FetchData fdata, URLContext context, WData wPageData, XPathStructure structure) ;
}
