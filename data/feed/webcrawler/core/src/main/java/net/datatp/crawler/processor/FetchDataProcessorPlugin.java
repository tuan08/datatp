package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.xpath.XPathStructure;

public interface FetchDataProcessorPlugin {
  public void process(FetchData fdata, URLContext context, XhtmlDocument xdoc, XPathStructure structure) ;
}
