package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.fetcher.Fetcher;
import net.datatp.xhtml.fetcher.HttpClientFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MainContentExtractorUnitTest {
  @Test
  public void test() throws Exception {
    String url = "http://www.aia.com.vn/vn/recruitment/for-consultants/agents/agent-recruitment" ;

    Fetcher fetcher = new HttpClientFetcher();
    XhtmlDocument xdoc = fetcher.fetch(url);

    DocumentExtractor extractor = new DocumentExtractor() ;
    TDocument tdoc = new TDocument("", url, xdoc.getXhtml()) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    //visitor.process(tdoc.getRoot()) ;
    ExtractContent extractContent = extractor.extract(null, tdoc) ;
    if(extractContent != null) {
      extractContent.dump(System.out) ;
    }
  }
}