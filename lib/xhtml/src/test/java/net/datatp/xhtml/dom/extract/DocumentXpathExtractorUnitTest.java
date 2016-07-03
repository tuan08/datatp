package net.datatp.xhtml.dom.extract;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.extract.DocumentXPathExtractor;
import net.datatp.xhtml.dom.extract.ExtractBlock;
import net.datatp.xhtml.dom.extract.ExtractContent;
import net.datatp.xhtml.extract.XpathConfig;

import org.junit.Assert;
import org.junit.Test;

public class DocumentXpathExtractorUnitTest {
  static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head>\n" +
    "    <base href='http://www.vnexpress.net'/>" +
    "    <title>Hello world</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <h3>A title</h3>\n" +
    "    <div class='description'>\n" +
    "      <p>the description<p>\n" +
    "    </div>\n" +
    "    <div class='content'>\n" +
    "      <p>content 1<p>\n" +
    "      <p>content 2<p>\n" +
    "      <p>content 3<p>\n" +
    "    </div>\n" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void test() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://localhost", HTML) ;
    XpathConfig config = new XpathConfig() ;
    config.setName("mainContent") ;
    config.addXpath("title",       "body/h3") ;
    config.addXpath("description", "body/div[class=description]/p") ;
    config.addXpath("content",     "body/div[class=content]") ;

    DocumentXPathExtractor extractor = new DocumentXPathExtractor() ;
    ExtractContent extractContent = extractor.extract(null, tdoc, new XpathConfig[] {config}) ;
    Assert.assertNotNull(extractContent) ;
    extractContent.dump(System.out) ;
    ExtractBlock mainContent = extractContent.getExtractBlock("mainContent") ;
    Assert.assertNotNull(mainContent) ;
    Assert.assertEquals("A title", mainContent.getTextContent("title"));
    Assert.assertEquals("the description", mainContent.getTextContent("description"));
    Assert.assertTrue(mainContent.getTextContent("content").indexOf("content 1") >= 0);
  }
}