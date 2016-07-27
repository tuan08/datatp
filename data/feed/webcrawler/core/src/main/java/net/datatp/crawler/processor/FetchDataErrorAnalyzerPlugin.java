package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.http.ResponseHeaders;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataErrorAnalyzerPlugin implements FetchDataProcessorPlugin {

  @Override
  public void process(FetchData fdata, URLContext context, WData wPageData, XPathStructure structure) {
    ResponseHeaders responseHeaders = fdata.getResponseHeaders();
    int rcode = responseHeaders.getResponseCode() ;
    if(rcode < 200)       wPageData.addTag("error:response:100") ;
    else if(rcode >= 500) wPageData.addTag("error:response:500") ;
    else if(rcode >= 400) wPageData.addTag("error:response:400") ;
    else if(rcode >= 300) wPageData.addTag("error:response:300") ;
    if(rcode != 200) return ;
    if(wPageData == null) return;
    
    String content = wPageData.getDataAsXhtml() ;
    if(content == null || content.length() < 1000) {
      wPageData.addTag("error:content:length") ;
    }
  }
}