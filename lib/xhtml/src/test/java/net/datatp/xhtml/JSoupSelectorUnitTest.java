package net.datatp.xhtml;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.xhtml.parser.JSoupParser;

public class JSoupSelectorUnitTest {

  @Test
  public void testHtmlParser() throws Exception {
    JSoupParser parser = new JSoupParser() ;
    Document doc = parser.parse(Html.STANDARD) ;
    
    Element body = doc.select("body").get(0);
    System.out.println(body.text());
    
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