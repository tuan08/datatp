package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.util.URLParser;

public class URLDirStructure {
  private String                domain;
  private String                directory;
  private List<URLParser>       urls      = new ArrayList<>();
  private Map<String, URLParam> urlParams = new HashMap<>();

  public URLDirStructure(String domain, String directory) {
    this.domain    = domain;
    this.directory = directory;
  }
  
  public String getDomain() { return domain; }

  public String getDirectory() { return directory; }

  public String[] getUrlInfos() {
    String[] urlInfos =  new String[urls.size()];
    for(int i = 0; i < urlInfos.length; i++) {
      urlInfos[i] = urls.get(i).toString();
    }
    return urlInfos;
  }
  
  public void add(URLParser urlParser) {
    analyze(urlParser);
  }
  
  void analyze(URLParser urlParser) {
    urls.add(urlParser);
    for(String paramKey : urlParser.getParamKeys()) {
      URLParam urlParam = urlParams.get(paramKey);
      if(urlParam == null) {
        urlParam = new URLParam(paramKey);
        urlParams.put(urlParam.name, urlParam);
      }
      urlParam.incrCount(1);
    }
  }
  
  public void dump(Appendable out) throws IOException {
    out.append(domain).append(" -").append(directory).append(":\n");
    for(URLParser sel : urls) {
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
