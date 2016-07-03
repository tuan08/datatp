package net.datatp.xhtml;

import org.junit.Test;
import org.w3c.dom.Document;

import net.datatp.xhtml.parser.NekoParser;
import net.datatp.xhtml.visitor.NodePrintVisitor;

public class NodePrintVisitorUnitTest {
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
    NodePrintVisitor visitor = new NodePrintVisitor(System.out) ;
    visitor.traverse(doc) ;
  }
}