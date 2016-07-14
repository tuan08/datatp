package net.datatp.webcrawler.analysis;

import javax.annotation.PostConstruct;

import org.springframework.jmx.export.annotation.ManagedResource;

import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;
/**
 * $Author: Tuan Nguyen$ 
 **/
@ManagedResource(
  objectName="org.headvances.analysis.xhtml:name=XHTMLErrorAnalyzer", 
  description="This component is responsible for finding the corrupted documents"
)
public class XHTMLErrorAnalyzer implements Analyzer {
  public XHTMLErrorAnalyzer() {
  }

  @PostConstruct
  public void onInit() throws Exception {
  }

  public void analyze(XhtmlDocument xdoc, TDocument tdoc) {
    int rcode = xdoc.getHeaders().getResponseCode() ;
    if(rcode < 200) xdoc.addTag("error:response:100") ;
    else if(rcode >= 500) xdoc.addTag("error:response:500") ;
    else if(rcode >= 400) xdoc.addTag("error:response:400") ;
    else if(rcode >= 300) xdoc.addTag("error:response:300") ;
    if(rcode != 200) return ;

    String content = xdoc.getXhtml() ;
    if(content == null || content.length() < 1000) {
      xdoc.addTag("error:content:length") ;
    }
  }
}