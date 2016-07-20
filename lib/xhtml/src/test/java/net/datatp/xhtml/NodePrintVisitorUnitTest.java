package net.datatp.xhtml;

import org.junit.Test;
import org.w3c.dom.Document;

import net.datatp.xhtml.parser.NekoParser;
import net.datatp.xhtml.visitor.NodePrintVisitor;

public class NodePrintVisitorUnitTest {
  @Test
  public void testHtmlParser() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(Html.STANDARD) ;
    NodePrintVisitor visitor = new NodePrintVisitor(System.out) ;
    visitor.traverse(doc) ;
  }
}