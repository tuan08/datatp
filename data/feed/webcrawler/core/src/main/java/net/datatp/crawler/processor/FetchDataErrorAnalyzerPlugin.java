package net.datatp.crawler.processor;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.http.ResponseHeaders;
import net.datatp.crawler.site.URLContext;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataErrorAnalyzerPlugin implements FetchDataProcessorPlugin {

  @Override
  public void process(FetchData fdata, URLContext context, XhtmlDocument xdoc, XPathStructure structure) {
    ResponseHeaders responseHeaders = fdata.getResponseHeaders();
    int rcode = responseHeaders.getResponseCode() ;
    if(rcode < 200)       xdoc.addTag("error:response:100") ;
    else if(rcode >= 500) xdoc.addTag("error:response:500") ;
    else if(rcode >= 400) xdoc.addTag("error:response:400") ;
    else if(rcode >= 300) xdoc.addTag("error:response:300") ;
    if(rcode != 200) return ;
    if(xdoc == null) return;
    
    String content = xdoc.getXhtml() ;
    if(content == null || content.length() < 1000) {
      xdoc.addTag("error:content:length") ;
    }
  }
}