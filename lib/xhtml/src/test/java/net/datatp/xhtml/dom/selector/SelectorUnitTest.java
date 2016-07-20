package net.datatp.xhtml.dom.selector;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.selector.CssClassSelector;
import net.datatp.xhtml.dom.selector.ElementIdSelector;
import net.datatp.xhtml.dom.selector.OrSelector;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TNodeSelector;
import net.datatp.xhtml.dom.selector.TagSelector;
import net.datatp.xhtml.dom.selector.TextSimilaritySelector;
import net.datatp.xhtml.dom.tagger.LinkBlockTagger;
import net.datatp.xhtml.dom.tagger.TitleBlockTagger;

import org.junit.Assert;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SelectorUnitTest {
  static String HTML = 
      "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
          "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
          "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
          "  <head>\n" +
          "    <base href='http://www.vnexpress.net'/>" +
          "    <title>Hello world</title>\n" +
          "  </head>\n" +
          "  <body>\n" +
          "    <h1>Hello world</h1\n" +
          "    <div>\n" +
          "      <ul class='ULActionBlock1' id='ULActionBlock1'>\n" +
          "        <li><a href=''><img src='img'/>Ý kiến bạn đọc(@#)1</a></li>\n" +
          "        <li><a href=''><img src='img'/>Thảo luận (7^#&)</a></li>\n" +
          "        <li><a href=''><img src='img'/>Phản hồi</a></li>\n" +
          "      </ul>\n" +
          "    </div>\n" +
          "    <div>\n" +
          "      <ul class='ULPageBlock1' id='ULPageBlock1'>\n" +
          "        <li><a href='/gl/the-gioi/'>27 người chết vì mưa lớn</a></li>\n" +
          "        <li><a href='/gl/vi-tinh/'>10% tên miền tiếng Việt</a></li>\n" +
          "        <li><a href='/gl/the-thao/'>Italy đất của mafia</a></li>\n" +
          "      </ul>\n" +
          "      <ul class='ULPageBlock2' id='ULPageBlock2'>\n" +
          "        <li><a href='/gl/the-gioi/'>27 người chết vì mưa lớn</a></li>\n" +
          "        <li><a href='/gl/vi-tinh/'>10% tên miền tiếng Việt</a></li>\n" +
          "        <li><a href='/gl/the-thao/'>Italy đất của mafia</a></li>\n" +
          "      </ul>\n" +      
          "    </div>\n" +
          "  </body>\n" +
          "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    TDocument tdoc = new TDocument("Hello world", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new LinkBlockTagger().tag(tdoc, tdoc.getRoot()) ;
    new TitleBlockTagger().tag(tdoc, root) ;

    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(tdoc.getRoot()) ;
    assertCssClassSelector(root) ;
    assertElementIdSelector(root) ;
    assertTagSelector(root) ;
    assertTextSimilaritySelector(root) ;
    assertTNodeSelector(root) ;
    assertOrSelector(root) ;
  }

  private void assertTagSelector(TNode root) {
    TNode[] node = root.select(new TagSelector(TitleBlockTagger.TITLE_CANDIDATE)) ;
    Assert.assertEquals(1, node.length) ;
    node = root.select(new TagSelector(LinkBlockTagger.BLOCK_LINK_ACTION)) ;
    Assert.assertEquals(1, node.length) ;
    node = root.select(new TagSelector(LinkBlockTagger.BLOCK_LINK_RELATED)) ;
    Assert.assertEquals(2, node.length) ;
  }

  private void assertCssClassSelector(TNode root) {
    TNode[] node = root.select(new CssClassSelector("ULActionBlock1")) ;
    Assert.assertEquals(1, node.length) ;

    String[] exp = { "ULActionBlock1", "ULPageBlock1" } ;
    node = root.select(new CssClassSelector(exp)) ;
    Assert.assertEquals(2, node.length) ;
  }

  private void assertElementIdSelector(TNode root) {
    TNode[] node = root.select(new ElementIdSelector("ULActionBlock1")) ;
    Assert.assertEquals(1, node.length) ;

    String[] exp = { "ULActionBlock1", "ULPageBlock1" } ;
    node = root.select(new ElementIdSelector(exp)) ;
    Assert.assertEquals(2, node.length) ;
  }

  private void assertTextSimilaritySelector(TNode root) {
    String[] text = {
        "Ý KIẾN BẠN DỌC", "Ý kiến bạn đọc", "Các ý kiến khác cùng", "Đóng góp ý kiến sản phẩm",
        "Thảo luận", "bình luận", "phản hồi", "Ý kiến bạn đọc", "Comments"
    };
    TNode[] node = root.select(new TextSimilaritySelector(text)) ;
    Assert.assertEquals(3, node.length) ;
  }

  private void assertTNodeSelector(TNode root) {
    TNode[] node = root.select(new TNodeSelector("a")) ;
    Assert.assertEquals(9, node.length) ;
  }

  private void assertURLPatternSelector(TNode root) {
    //TODO: assert URLPatternSelector
  }

  private void assertOrSelector(TNode root) {
    Selector selector = 
        new OrSelector(new ElementIdSelector("ULActionBlock1"), new CssClassSelector("ULPageBlock2")) ;
    TNode[] node = root.select(selector) ;
    Assert.assertEquals(2, node.length) ;
  }
}