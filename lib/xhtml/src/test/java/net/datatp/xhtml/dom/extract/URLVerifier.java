package net.datatp.xhtml.dom.extract;

import org.junit.Assert;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLVerifier {
  private String   anchorText ;
  private String   url ;
  private DocumentExtractor.Type type ;
  private String[] expectTags ;

  public URLVerifier(String anchorText, String url, 
      DocumentExtractor.Type type, String[] expectTag) {
    this.anchorText = anchorText ;
    this.url = url ;
    this.type = type ;
    this.expectTags = expectTag ;
  }

  public ExtractContent extract(SimpleHttpFetcher fetcher) throws Exception {
    XhtmlDocument xdoc = fetcher.fetch(url);
    DocumentExtractor extractor = new DocumentExtractor() ;
    TDocument tdoc = new TDocument(url, anchorText, xdoc.getXhtml()) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    //visitor.process(tdoc.getRoot()) ;
    ExtractContent extractContent = extractor.extract(type, tdoc) ;
    return extractContent ;
  }

  public void verify(SimpleHttpFetcher fetcher, boolean dump) throws Exception {
    ExtractContent extractContent = extract(fetcher) ;
    if(dump) extractContent.dump(System.out) ;
    ExtractBlock mainBlock = extractContent.getExtractBlock("mainContent") ;
    Assert.assertNotNull(mainBlock) ;
    String[] tags = mainBlock.getTags() ;
    for(String expectTag : expectTags) {
      boolean foundTag = false ;
      for(String tag : tags) {
        if(tag.equals(expectTag)) {
          foundTag = true ;
          break ;
        }
      }
      if(!foundTag) Assert.fail("Expect tag " + expectTag) ;
    }
  }
}
