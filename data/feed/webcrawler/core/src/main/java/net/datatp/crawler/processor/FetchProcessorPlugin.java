package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.xhtml.extract.WDataExtractContext;

public interface FetchProcessorPlugin {
  public void process(FetchContext fdata, WDataExtractContext context) ;
}
