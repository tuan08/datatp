package net.datatp.crawler.processor;

import net.datatp.crawler.XDocMapper;
import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.crawler.http.ResponseHeaders;
import net.datatp.xhtml.extract.WDataContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchErrorAnalyzerPlugin implements FetchProcessorPlugin {

  @Override
  public void process(FetchContext fetchCtx, WDataContext wdataCtx) {
    XDocMapper xdoc = fetchCtx.getXDocMapper();
    ResponseHeaders responseHeaders = fetchCtx.getResponseHeaders();
    int rcode = responseHeaders.getResponseCode() ;
    
    if(rcode >= 500)       xdoc.setErrorResponseCode("500");
    else if(rcode >= 400)  xdoc.setErrorResponseCode("400");
    else if(rcode >= 300)  xdoc.setErrorResponseCode("300");
    else if(rcode < 200)   xdoc.setErrorResponseCode("100");
    if(rcode != 200) return ;
    
    if(wdataCtx == null) return;
    
    String content = wdataCtx.getWdata().getDataAsXhtml() ;
    if(content == null || content.length() < 1000) {
      xdoc.setErrorContent("length");
    }
  }
}