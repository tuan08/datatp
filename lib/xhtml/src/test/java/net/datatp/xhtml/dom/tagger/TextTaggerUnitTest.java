package net.datatp.xhtml.dom.tagger;

import junit.framework.Assert ;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.processor.CleanEmptyNodeProcessor;
import net.datatp.xhtml.dom.processor.TNodePrinter;
import net.datatp.xhtml.dom.selector.TagSelector;
import net.datatp.xhtml.dom.tagger.TextBlockTagger;

import org.junit.Test ;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextTaggerUnitTest {
	static String LONG_TEXT = 
		"Cho tới nay, Bộ Y tế đã phối hợp Bộ Tài chính cùng một số bộ, cơ quan khác xây dựng Nghị " +
		"định về cơ chế hoạt động, cơ chế tài chính đối với các đơn vị sự nghiệp y tế công lập và " + 
		"giá dịch vụ khám, chữa bệnh công lập. Bộ Y tế cũng có tờ trình Chính Phủ xem xét từ tháng " + 
		"11/2010 (khi đó Bộ trưởng Y tế là ông Nguyễn Quốc Triệu). Trước đó, dự thảo tăng giá viện " + 
		"phí được Bộ Y tế đưa ra lấy ý kiến của các bộ ngành, bệnh viện và người dân."  ;
	static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <body>\n" +
    "    <div>\n" +
    "      <div>\n" +
    "        <div  id='DivBlockText1'>\n" +
    "          <p>[1]Cho tới nay</p>" +
    "          <div>\n" +
    "            <div>\n" +
    "              <div>\n" +
    "                <div id='DivBlockText2'>\n" +
    "                  <p>[1]" + LONG_TEXT + "</p>" +
    "                  <p>[2]" + LONG_TEXT + "</p>" +
    "                  <p>[3]" + LONG_TEXT + "</p>" +
    "                <div>\n" +
    "              <div>\n" +
    "            <div>\n" +
    "          <div>\n" +
    "        </div>\n" + 
    "      </div>\n" +
    "      <div id='ShortText'>\n" +
    "        <p>Cho tới nay, Bộ Y tế </p>" +
    "        <p>Cho tới nay, Bộ Y tế </p>" +
    "        <p>Cho tới nay, Bộ Y tế </p>" +
    "      </div>\n" +
    "    </div>\n" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new TextBlockTagger().tag(tdoc, tdoc.getRoot()) ;
    TNodePrinter visitor = new TNodePrinter(System.out) ;
    visitor.process(tdoc.getRoot()) ;
    assertBlockTextTagger(root) ;
  }
  
  private void assertBlockTextTagger(TNode root) {
  	TNode[] nodes = root.select(new TagSelector(TextBlockTagger.BLOCK_TEXT)) ;
    Assert.assertEquals(1, nodes.length) ;
    Assert.assertTrue("DivBlockText2".equalsIgnoreCase(nodes[0].getElementId())) ;
  }
}