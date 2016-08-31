package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.TreeMap;

public class URLSiteStructure {
  private TreeMap<String, URLDomainStructure> domainStructures = new TreeMap<>();

  public TreeMap<String, URLDomainStructure> getDomainStructures() { return this.domainStructures; }
  
  public void add(URLAnalysis urlAnalysis) {
    URLDomainStructure domainStructure = domainStructures.get(urlAnalysis.getUrlInfo().getHost());
    if(domainStructure == null) {
      domainStructure = new URLDomainStructure(urlAnalysis.getUrlInfo().getHost());
      domainStructures.put(urlAnalysis.getUrlInfo().getHost(), domainStructure);
    }
    domainStructure.add(urlAnalysis);
  }
  
  public void clear() {
    domainStructures.clear();
  }
  
  public void dump(Appendable out) throws IOException {
    for(URLDomainStructure sel : domainStructures.values()) {
      sel.dump(out);
    }
  }
}