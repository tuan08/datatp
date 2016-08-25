package net.datatp.crawler.site.analysis;

import net.datatp.util.URLAnalyzer;

public class URLStructure {
  private URLAnalyzer urlAnalyzer;
  private String      xhtml;
  
  public URLStructure() {}
  
  public URLStructure(URLAnalyzer urlAnalyzer, String xhtml) {
    this.urlAnalyzer = urlAnalyzer;
    this.xhtml       = xhtml;
  }
  
  public URLAnalyzer getUrlAnalyzer() { return urlAnalyzer; }
  public void setUrlAnalyzer(URLAnalyzer urlAnalyzer) { this.urlAnalyzer = urlAnalyzer; }
  
  public String getXhtml() { return xhtml; }
  public void setXhtml(String xhtml) { this.xhtml = xhtml; }
}
