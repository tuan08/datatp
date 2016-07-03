package net.datatp.xhtml.dom.tagger;

import junit.framework.Assert;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.selector.TagSelector;
import net.datatp.xhtml.dom.tagger.LinkBlockTagger;

import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class LinkTaggerUnitTest {
	static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <body>\n" +
    "    <div>\n" +
    "      <ul class='ULActionBlock1' id='ULActionBlock1'>\n" +
    "        <li><a href=''><img src='img'/>action 1</a></li>\n" +
    "        <li>\n" +
    "          <div><a href=''>action 2.2</a></div>\n" +
    "        </li>\n" +
    "        <li>\n" +
    "          <a href=''>action 2.3</a>\n" +
    "          <ul class='ULActionBlock2'>\n" +
    "            <li><a href=''>action 5</a></li>\n" +
    "            <li><a href=''>action 6</a></li>\n" +
    "          </ul>\n" +
    "        </li>\n" +
    "      </ul>\n" +
    "    </div>\n" +
    
    "    <div id='DivPageBlock1'>\n" +
    "      <a href=''>[1]this should be a link page</a>\n" +
    "      <a href=''>[2]this should be a link page</a>\n" +
    "      <div>\n" +
    "        <div>\n" +
    "          <div>\n" +
    "            <a href=''>[3]this should be a link page</a>\n" +
    "            <a href=''>[4]this should be a link page</a>\n" +
    "          </div>\n" +
    "        </div>\n" +
    "      </div>\n" +
    "    </div>\n" +
    "    <div id='DivPageBlock2'>\n" +
    "      <a href=''>this should be a link page 2</a>\n" +
    "      <a href=''>this should be a link page 2</a>\n" +
    "    </div>\n" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    
    new CleanEmptyNodeProcessor().process(root) ;
    new LinkBlockTagger().tag(tdoc, root) ;
    
    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(root) ;
    assertActionLinkTagger(root) ;
  }
  
  private void assertActionLinkTagger(TNode root) {
    TNode[] nodes = 
    	root.select(new TagSelector(LinkBlockTagger.BLOCK_LINK_ACTION), false) ;
    Assert.assertEquals(2, nodes.length) ;
    Assert.assertTrue("ULActionBlock1".equalsIgnoreCase(nodes[0].getCssClass()));
    
    nodes = root.select(new TagSelector(LinkBlockTagger.BLOCK_LINK_RELATED)) ;
    Assert.assertEquals(2, nodes.length) ;
    Assert.assertTrue("DivPageBlock1".equalsIgnoreCase(nodes[0].getElementId()));
  }
}