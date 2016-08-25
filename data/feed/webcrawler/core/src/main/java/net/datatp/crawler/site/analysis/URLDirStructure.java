package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.datatp.util.URLAnalyzer;

public class URLDirStructure {
  private String                domain;
  private String                directory;
  private List<URLAnalyzer>     urls      = new ArrayList<>();

  public URLDirStructure(String domain, String directory) {
    this.domain    = domain;
    this.directory = directory;
  }
  
  public String getDomain() { return domain; }

  public String getDirectory() { return directory; }

  public List<URLAnalyzer> getUrls() { return this.urls; }
  
  public void add(URLAnalyzer urlParser) {
    analyze(urlParser);
  }
  
  void analyze(URLAnalyzer urlParser) {
    urls.add(urlParser);
  }
  
  public void dump(Appendable out) throws IOException {
    out.append(domain).append(" -").append(directory).append(":\n");
    for(URLAnalyzer sel : urls) {
      out.append("  ").append(sel.toString()).append("\n");
    }
  }
  
  static public class URLParam {
    private String  name;
    private int     count;
    private boolean ignoreURL = false;
    
    public URLParam() {}
    
    URLParam(String name) { this.name = name ; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public boolean isIgnoreURL() { return ignoreURL; }
    public void setIgnoreURL(boolean ignoreURL) { this.ignoreURL = ignoreURL; }
    
    public void incrCount(int incr) { count += incr; }
  }
}
