package net.datatp.crawler.site.analysis;

import net.datatp.util.URLInfo;

public class URLAnalysis {
  private String  pageTypeCategory = "uncategorized";
  private URLInfo urlInfo;

  public String getPageTypeCategory() { return pageTypeCategory; }
  public void setPageTypeCategory(String pageTypeCategory) { this.pageTypeCategory = pageTypeCategory; }
  
  public URLInfo getUrlInfo() { return urlInfo; }
  public void setUrlInfo(URLInfo urlInfo) { this.urlInfo = urlInfo; }
  
  
}
