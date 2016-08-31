package net.datatp.crawler.site.analysis;

import net.datatp.util.URLInfo;

public class URLData {
  private URLInfo urlInfo;
  private String      xhtml;
  
  public URLData() {}
  
  public URLData(URLInfo urlInfo, String xhtml) {
    this.urlInfo = urlInfo;
    this.xhtml       = xhtml;
  }
  
  public URLInfo getUrlInfo() { return urlInfo; }
  public void setUrlInfo(URLInfo urlInfo) { this.urlInfo = urlInfo; }
  
  public String getXhtml() { return xhtml; }
  public void setXhtml(String xhtml) { this.xhtml = xhtml; }
}
