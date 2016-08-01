package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.http.ResponseHeaders;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.WDataContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataErrorAnalyzerPlugin implements FetchDataProcessorPlugin {

  @Override
  public void process(FetchData fdata, URLContext urlContext, WDataContext context) {
    WData wData = context.getWdata();
    ResponseHeaders responseHeaders = fdata.getResponseHeaders();
    int rcode = responseHeaders.getResponseCode() ;
    if(rcode < 200)       wData.addTag("error:response:100") ;
    else if(rcode >= 500) wData.addTag("error:response:500") ;
    else if(rcode >= 400) wData.addTag("error:response:400") ;
    else if(rcode >= 300) wData.addTag("error:response:300") ;
    if(rcode != 200) return ;
    if(wData == null) return;
    
    String content = wData.getDataAsXhtml() ;
    if(content == null || content.length() < 1000) {
      wData.addTag("error:content:length") ;
    }
  }
}