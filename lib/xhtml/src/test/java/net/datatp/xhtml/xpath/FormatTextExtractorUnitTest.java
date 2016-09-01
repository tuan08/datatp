package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Document;
import org.junit.Test;

import net.datatp.xhtml.Html;
import net.datatp.xhtml.JSoupParser;

public class FormatTextExtractorUnitTest {

  @Test
  public void testHtmlParser() throws Exception {
    JSoupParser parser = new JSoupParser() ;
    Document doc = parser.parse(Html.STANDARD) ;
    FormatTextExtractor extractor = new FormatTextExtractor();
    System.out.println(extractor.extract(doc));
  }
}