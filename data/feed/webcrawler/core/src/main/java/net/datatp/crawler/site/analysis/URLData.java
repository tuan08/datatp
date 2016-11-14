package net.datatp.crawler.site.analysis;

import net.datatp.util.URLInfo;

public class URLData {
  private URLInfo urlInfo;
  private String  xhtml;
  private WebPageAnalysis webPageAnalysis;
  
  public URLData() {}
  
  public URLData(URLInfo urlInfo, String xhtml) {
    this.urlInfo = urlInfo;
    this.xhtml       = xhtml;
  }
  
  public URLInfo getUrlInfo() { return urlInfo; }
  public void setUrlInfo(URLInfo urlInfo) { this.urlInfo = urlInfo; }
  
  public String getXhtml() { return xhtml; }
  public void setXhtml(String xhtml) { this.xhtml = xhtml; }

  public WebPageAnalysis getWebPageAnalysis() { return webPageAnalysis; }
  public void setWebPageAnalysis(WebPageAnalysis webpageAnalysis) { this.webPageAnalysis = webpageAnalysis; }
}