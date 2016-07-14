package net.datatp.xhtml.extract;

import net.datatp.xhtml.XhtmlDocument;

abstract public class Extractor {
  private String name;
  private String description;
  
  abstract public Extract extract(XhtmlDocument xdoc) ;
}
