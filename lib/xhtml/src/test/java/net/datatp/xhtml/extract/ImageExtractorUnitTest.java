package net.datatp.xhtml.extract;

import java.io.PrintWriter;
import java.net.URL;

import org.junit.Test;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

public class ImageExtractorUnitTest {
  
  @Test
  public void test() {
    System.err.println("start.....");
    try {
      URL url = new URL("http://vnexpress.net/tin-tuc/the-gioi/trieu-tien-tuyen-bo-khong-khuat-phuc-truoc-su-ham-doa-tu-my-3466191.html");

      final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

      final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();

      PrintWriter out = new PrintWriter("/tmp/highlighted.html", "UTF-8");
      out.println("<base href=\"" + url + "\" >");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");

      String extractedHtml = hh.process(url, extractor);
      out.println(extractedHtml);
      out.close();
    } catch(Throwable t) {
      t.printStackTrace();
    }
    System.err.println("end.....");
  }
}
