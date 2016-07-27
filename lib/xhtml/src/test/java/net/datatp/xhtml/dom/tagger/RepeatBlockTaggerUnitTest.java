package net.datatp.xhtml.dom.tagger;

import org.junit.Test;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RepeatBlockTaggerUnitTest {
  @Test
  public void testSite() throws Exception  {
    String url      = "http://mediamart.vn/ProductDetail.aspx?ProductId=11623";    
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData  xdoc  = fetcher.fetch(url);
    TDocument tdoc  = new TDocument(url, url, xdoc.getDataAsXhtml()) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    TNode[] nodes = new RepeatBlockTagger().tag(tdoc, root) ;
    for(TNode sel : nodes) {
      //visitor.process(sel) ;
      //System.out.println("-------------------------------------------------------");
      System.out.println(sel.getXPath() + ", " + sel.getTextSize());
      System.out.println(sel);
    }
  }
}