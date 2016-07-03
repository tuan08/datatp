package net.datatp.xhtml.dom.tagger;

import junit.framework.Assert ;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.selector.TagSelector;
import net.datatp.xhtml.dom.tagger.KeywordBlockTagger;

import org.junit.Test ;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class KeywordTaggerUnitTest {
	static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <body>\n" +
    "    <div>\n" +
    "      <ul class='ULActionBlock1' id='ULActionBlock1'>\n" +
    "        <li><a href=''><img src='img'/>hà nội 1</a></li>\n" +
    "        <li><a href=''><img src='img'/>hà tây &^@#</a></li>\n" +
    "        <li><a href=''><img src='img'/>hà đông</a></li>\n" +
    "      </ul>\n" +
    "    </div>\n" +
    "    <div>\n" +
    "       <a href=''>hải phòng</a>" +
    "    </div>\n" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testKeywordTagger() throws Exception {
    String[] keyword = {"hà nội", "hà đông", "hà tây", "thành phố nam định"} ;
  	TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new KeywordBlockTagger("block:keyword", keyword).tag(tdoc, root) ;
    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(tdoc.getRoot()) ;
    assertKeyWordBlockTagger(root) ;
  }
  
  private void assertKeyWordBlockTagger(TNode root) {
    TNode[] node = root.select(new TagSelector("block:keyword")) ;
    Assert.assertEquals(1, node.length) ;
  }
}