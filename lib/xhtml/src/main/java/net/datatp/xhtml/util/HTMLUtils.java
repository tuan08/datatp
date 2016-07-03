package net.datatp.xhtml.util;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLUtils {
  public static Set<String> extractLinks(String content) {
    final Set<String> result = new HashSet<String>();

    Document doc;
    try {
      doc = Jsoup.parse(content);
      Elements links = doc.select("a[href]");
      Elements media = doc.select("[src]");
      Elements imports = doc.select("link[href]");

      // href ...
      for (Element link : links) {
        result.add(link.attr("href"));
      }

      // img ...
      for (Element src : media) {
        result.add(src.attr("src"));
      }

      // js, css, ...
      for (Element link : imports) {
        result.add(link.attr("href"));
      }

    } catch (Exception e) {

    }
    result.remove("");
    return result;
  }
}
