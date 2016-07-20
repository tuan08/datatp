package net.datatp.xhtml;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.datatp.xhtml.TransformDomWriter;
import net.datatp.xhtml.parser.NekoParser;
import net.datatp.xhtml.visitor.NodeDeleteVisitor;

public class HtmlParserUnitTest {

  @Test
  public void testHtmlParser() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(Html.STANDARD) ;
    HtmlAssert.assertLink(doc, "AbsoluteLink", "/static/link/ABCDE") ;
    HtmlAssert.assertNodeContent(doc, "AbsoluteLink", "Hello") ;
    TransformDomWriter writer = new TransformDomWriter() ;
    writer.write(System.out, doc) ;
  }
  
  @Test
  public void testNodeDeleteVisitor() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(Html.STANDARD) ;
    HtmlAssert.assertNode(doc, "Script") ;
    NodeDeleteVisitor visitor = new NodeDeleteVisitor() {
      protected boolean shouldDelete(Node node) {
        return "script".equals(node.getNodeName());
      }
    } ;
    visitor.traverse(doc) ;
    HtmlAssert.assertNoNode(doc, "Script") ;
  }
}