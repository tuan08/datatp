package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.TreeMap;

import net.datatp.util.URLAnalyzer;

public class URLSiteStructure {
  private TreeMap<String, URLDomainStructure> domainStructures = new TreeMap<>();

  public TreeMap<String, URLDomainStructure> getDomainStructures() { return this.domainStructures; }
  
  public void add(URLAnalyzer urlParser) {
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
