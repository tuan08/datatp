package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.TreeMap;

import net.datatp.util.URLAnalyzer;

public class URLDomainStructure {
  private String domain;
  private TreeMap<String, URLDirStructure> dirStructures = new TreeMap<>();
  
  public URLDomainStructure(String domain) {
    this.domain = domain;
  }
  
  public String getDomain() { return this.domain; }
  
  public TreeMap<String, URLDirStructure> getDirectoryStructure() { return this.dirStructures; }
  
  public void add(URLAnalyzer urlParser) {
    URLDirStructure dirStructure = dirStructures.get(urlParser.getDirectory());
    if(dirStructure == null) {
      dirStructure = new URLDirStructure(domain, urlParser.getDirectory());
      dirStructures.put(urlParser.getDirectory(), dirStructure);
    }
    dirStructure.add(urlParser);
  }
  
  public void dump(Appendable out) throws IOException {
    for(URLDirStructure sel : dirStructures.values()) {
      sel.dump(out);
    }
  }
}
