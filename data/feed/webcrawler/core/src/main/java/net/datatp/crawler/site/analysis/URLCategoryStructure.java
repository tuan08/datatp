package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class URLCategoryStructure {
  private String                       domain;
  private String                       category;
  private TreeMap<String, URLAnalysis> urls = new TreeMap<>();

  public URLCategoryStructure(String domain, String category) {
    this.domain    = domain;
    this.category = category;
  }
  
  public String getDomain() { return domain; }

  public String getCategory() { return category; }

  public List<URLAnalysis> getUrls() { 
    ArrayList<URLAnalysis> holder = new ArrayList<>();
    holder.addAll(urls.values());
    return holder; 
  }
  
  public void add(URLAnalysis urlAnalysis) {
    analyze(urlAnalysis);
  }
  
  void analyze(URLAnalysis urlAnalysis) {
    urls.put(urlAnalysis.getUrlInfo().getPathWithParams(), urlAnalysis);
  }
  
  public void dump(Appendable out) throws IOException {
    out.append(domain).append(" -").append(category).append(":\n");
    for(URLAnalysis sel : urls.values()) {
      out.append("  ").append(sel.toString()).append("\n");
    }
  }
}
