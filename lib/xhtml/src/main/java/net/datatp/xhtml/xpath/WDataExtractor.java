package net.datatp.xhtml.xpath;

public class WDataExtractor {
  private String           name;
  private XPathExtractor[] extractors;
  
  public WDataExtractor(String name, XPathExtractor ... extractors) {
    this.extractors = extractors;
  }
  
  public String getName() { return this.name; }
  
  public int extract(WDataContext context) {
    int extractCount = 0 ;
    for(int i = 0; i < extractors.length; i++) {
      extractCount += extractors[i].extract(context);
    }
    return extractCount;
  }
}
