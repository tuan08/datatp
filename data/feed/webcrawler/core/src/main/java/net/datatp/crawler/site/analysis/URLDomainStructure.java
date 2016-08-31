package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.TreeMap;

public class URLDomainStructure {
  private String domain;
  private TreeMap<String, URLCategoryStructure> categoryStructures = new TreeMap<>();
  
  public URLDomainStructure(String domain) {
    this.domain = domain;
  }
  
  public String getDomain() { return this.domain; }
  
  public TreeMap<String, URLCategoryStructure> getCategoryStructure() { return this.categoryStructures; }
  
  public void add(URLAnalysis urlAnalysis) {
    URLCategoryStructure categoryStructure = categoryStructures.get(urlAnalysis.getPageTypeCategory());
    if(categoryStructure == null) {
      categoryStructure = new URLCategoryStructure(domain, urlAnalysis.getPageTypeCategory());
      categoryStructures.put(categoryStructure.getCategory(), categoryStructure);
    }
    categoryStructure.add(urlAnalysis);
  }
  
  public void dump(Appendable out) throws IOException {
    for(URLCategoryStructure sel : categoryStructures.values()) {
      sel.dump(out);
    }
  }
}
