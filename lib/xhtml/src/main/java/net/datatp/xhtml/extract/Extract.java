package net.datatp.xhtml.extract;

import org.jsoup.nodes.Element;

public class Extract {
  private Extractor extractor;
  private Element[] elements;
  
  public Extract(Extractor extractor, Element[] elements) {
    this.extractor = extractor;
    this.elements  = elements;
  }
  
  public Extractor getExtractor() { return this.extractor; }
  
  public Element[] getElements() { return this.elements; }
}
