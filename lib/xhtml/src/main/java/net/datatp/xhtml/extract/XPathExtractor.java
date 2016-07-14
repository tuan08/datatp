package net.datatp.xhtml.extract;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.datatp.xhtml.XhtmlDocument;

public class XPathExtractor extends Extractor {
  /**
   * Xpath query expression, examples:
   * html > body > a[class=Link] , html body a[class=Link]
   */
  private String xpathQuery;
  
  public Extract extract(XhtmlDocument xdoc) {
    Document  doc = xdoc.getJsoupDocument();
    Elements  founds = doc.select(xpathQuery);
    if(founds.size() == 0) return null;
    Element[] element = new Element[founds.size()];
    for(int i = 0; i < element.length; i++) {
      element[i] = founds.get(i);
    }
    return new Extract(this, element);
  }
}
