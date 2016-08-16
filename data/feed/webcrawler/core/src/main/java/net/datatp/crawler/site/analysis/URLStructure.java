package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.TreeMap;

import net.datatp.util.URLParser;

public class URLStructure {
  private TreeMap<String, URLDomainStructure> domainStructures = new TreeMap<>();

  
  public void add(URLParser urlParser) {
    URLDomainStructure domainStructure = domainStructures.get(urlParser.getHost());
    if(domainStructure == null) {
      domainStructure = new URLDomainStructure(urlParser.getHost());
      domainStructures.put(urlParser.getHost(), domainStructure);
    }
    domainStructure.add(urlParser);
  }
  
  public void dump(Appendable out) throws IOException {
    for(URLDomainStructure sel : domainStructures.values()) {
      sel.dump(out);
    }
  }
}
