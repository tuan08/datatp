package net.datatp.xhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSoupParser  {
  final static public JSoupParser INSTANCE = new JSoupParser() ;

  public JSoupParser() {
  }

  public Document parse(String xhtml) {
    Document doc = Jsoup.parse(xhtml);
    return doc ;
  }
}