package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.dom.TDocument;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MainContentExtractorUnitTest {
  @Test
  public void test() throws Exception {
    String url = "http://www.aia.com.vn/vn/recruitment/for-consultants/agents/agent-recruitment" ;

    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData wPageData = fetcher.fetch(url);

    DocumentExtractor extractor = new DocumentExtractor() ;
    TDocument tdoc = new TDocument("", url, wPageData.getDataAsXhtml()) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    //visitor.process(tdoc.getRoot()) ;
    ExtractContent extractContent = extractor.extract(null, tdoc) ;
    if(extractContent != null) {
      extractContent.dump(System.out) ;
    }
  }
}