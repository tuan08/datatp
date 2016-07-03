package net.datatp.xhtml;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.xhtml.parser.JSoupParser;

public class JSoupSelectorUnitTest {
  static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head>\n" +
    "    <base href='http://www.vnexpress.net'/>" +
    "    <title>Hello world</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <a id='AbsoluteLink' class='Link' href='/static/link/ABCDE'>Hello</a>\n" +
    "    <a id='RelativeLink' class='Link' href='link/ABCDE'>Hello</a>\n" +
    "    <A id='ExternalLink' class='Link ExternalLink' href='http://vnexpress.net/static/link/ABCDE'>Hello</A>\n" +
    "    <img id='AbsoluteImgSrc' href='/image/image.jpg' />\n" +
    "    <script id='Script' type='text/javascript'>" +
    "      window.alert('hello') ;" +
    "    </script>" +
    "    <br />" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testHtmlParser() throws Exception {
    JSoupParser parser = new JSoupParser() ;
    Document doc = parser.parse(HTML) ;
    Elements founds = doc.select("a[href]"); // a with href
    Assert.assertEquals(3, founds.size()) ;
    
    founds = doc.select("html > body > a[class=Link]"); // a with href
    Assert.assertEquals(2, founds.size()) ;
    
    founds = doc.select("html body a[class=Link]"); // a with href
    Assert.assertEquals(2, founds.size()) ;
    
    founds = doc.select("html > body > a:eq(1)"); // a with href
    Assert.assertEquals(1, founds.size()) ;

    
    founds = doc.select("a.Link"); // a with href
    Assert.assertEquals(3, founds.size()) ;
    
    founds = doc.select("a[class=Link]"); // a with href
    Assert.assertEquals(2, founds.size()) ;
    
    founds = doc.select("a[class=Link ExternalLink]"); // a with href
    Assert.assertEquals(1, founds.size()) ;
    
    founds = doc.select("body > a[class=Link ExternalLink]"); // a with href
    Assert.assertEquals(1, founds.size()) ;
  }
}