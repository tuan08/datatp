package net.datatp.xhtml.dom;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.tagger.LinkBlockTagger;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeUnitTest {
	static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head>\n" +
    "    <base href='http://www.vnexpress.net'/>" +
    "    <title>Hello world</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <div><span><i></i></span></div>\n" +
    "    <div><span><i>test</i></span></div>\n" +
    
    "    <div>\n" +
    "      <ul class='ULActionBlock' id='ULActionBlock'>\n" +
    "        <li><a href=''><img src='img'/></a></li>\n" +
    "        <li><a href=''><img src='img'/></a></li>\n" +
    "        <li><a href=''><img src='img'/></a></li>\n" +
    "      </ul>\n" +
    "    </div>\n" +
    
    "    <a id='AbsoluteLink' href='/static/link/ABCDE'>Hello</a>\n" +
    "    <a id='RelativeLink' href='link/ABCDE'>Hello</a>\n" +
    "    <A id='ExternalLink' href='http://vnexpress.net/static/link/ABCDE'>Hello</A>\n" +
    "    <img id='AbsoluteImgSrc' href='/image/image.jpg' />\n" +
    "    <script id='Script' type='text/javascript'>" +
    "      window.alert('hello') ;" +
    "    </script>" +
    "    <br />" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    new CleanEmptyNodeProcessor().process(tdoc.getRoot()) ;
    new LinkBlockTagger().tag(tdoc, tdoc.getRoot()) ;
    
    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(tdoc.getRoot()) ;
    
    TNode node = tdoc.getRoot().findFirstDescendantByCssClass("ULActionBlock") ;
    Assert.assertTrue(node.hasTag("block:link:action")) ;
    Assert.assertEquals("ULActionBlock".toLowerCase(), node.getCssClass()) ;
    Assert.assertEquals(1, tdoc.getRoot().findDescendantByCssClass("ULActionBlock").size()) ;
    
    node = tdoc.getRoot().findFirstDescendantByElementId("ULActionBlock") ;
    Assert.assertEquals("ULActionBlock".toLowerCase(), node.getCssClass()) ;
    Assert.assertEquals(1, tdoc.getRoot().findDescendantByElementId("ULActionBlock").size()) ;
    
  }
}
