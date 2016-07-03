package net.datatp.xhtml;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.datatp.xhtml.TransformDomWriter;
import net.datatp.xhtml.parser.NekoParser;
import net.datatp.xhtml.visitor.NodeDeleteVisitor;

public class HtmlParserUnitTest {
  static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head>\n" +
    "    <base href='http://www.vnexpress.net'/>" +
    "    <title>Hello world</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
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
  public void testHtmlParser() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(HTML) ;
    HtmlAssert.assertLink(doc, "AbsoluteLink", "/static/link/ABCDE") ;
    HtmlAssert.assertNodeContent(doc, "AbsoluteLink", "Hello") ;
    TransformDomWriter writer = new TransformDomWriter() ;
    writer.write(System.out, doc) ;
  }
  
  @Test
  public void testNodeDeleteVisitor() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(HTML) ;
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