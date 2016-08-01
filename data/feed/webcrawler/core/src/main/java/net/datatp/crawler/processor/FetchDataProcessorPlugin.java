package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.xpath.WDataContext;

public interface FetchDataProcessorPlugin {
  public void process(FetchData fdata, URLContext urlContext, WDataContext context) ;
}
