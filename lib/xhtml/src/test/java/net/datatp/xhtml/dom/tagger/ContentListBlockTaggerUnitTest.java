package net.datatp.xhtml.dom.tagger;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.selector.TagSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ContentListBlockTaggerUnitTest {
  static String LONG_TEXT = 
      "Cho tới nay, Bộ Y tế đã phối hợp Bộ Tài chính cùng một số bộ, cơ quan khác xây dựng Nghị " +
      "định về cơ chế hoạt động, cơ chế tài chính đối với các đơn vị sự nghiệp y tế công lập.";

  static String HTML = 
      "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
      "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
      "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
      "  <body>\n"   +
      "    <div id='BlockTextList'>\n"  +
      "      <div>\n"  +
      "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
      "         <p>" + LONG_TEXT + "</p>" +
      "      </div>\n" +
      "      <div>\n"  +
      "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
      "         <p>" + LONG_TEXT + "</p>" +
      "      </div>\n" +
      "      <div>\n"  +
      "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
      "         <p>" + LONG_TEXT + "</p>" +
      "      </div>\n" +
      "      <div>\n"  +
      "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
      "         <p>" + LONG_TEXT + "</p>" +
      "      </div>\n" +
      "      <div>\n"  +
      "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
      "         <p>" + LONG_TEXT + "</p>" +
      "      </div>\n" +
      "    </div>\n" +
      "  </body>\n"  +
      "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new ContentListBlockTagger().tag(tdoc, tdoc.getRoot()) ;
    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(tdoc.getRoot()) ;
    assertContentListBlockTagger(root) ;
  }

  private void assertContentListBlockTagger(TNode root) {
    TNode[] nodes = root.select(new TagSelector(ContentListBlockTagger.BLOCK_LIST)) ;
    Assert.assertEquals(1, nodes.length) ;
    Assert.assertTrue("BlockTextList".equalsIgnoreCase(nodes[0].getElementId())) ;
  }

  //@Test
  public void testSite() throws Exception  {
    String url = "http://dantri.com.vn/";    
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData xdoc = fetcher.fetch(url);
    TDocument tdoc = new TDocument(url, "", xdoc.getDataAsXhtml()) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new ContentListBlockTagger().tag(tdoc, root) ;

    TNode[] nodes = root.select(new TagSelector(ContentListBlockTagger.BLOCK_LIST)) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    for(TNode sel : nodes) {
      //visitor.process(sel) ;
      System.out.println(sel.getTextContent());
    }
  }
}