package net.datatp.crawler.site.analysis;

import java.util.List;

import net.datatp.util.URLInfo;
import net.datatp.xhtml.extract.ExtractEntity;

public class URLAnalysis {
  private String   pageTypeCategory = "uncategorized";
  private URLInfo  urlInfo;
  private String[] extractEntityInfo;
  
  public String getPageTypeCategory() { return pageTypeCategory; }
  public void setPageTypeCategory(String pageTypeCategory) { this.pageTypeCategory = pageTypeCategory; }
  
  public URLInfo getUrlInfo() { return urlInfo; }
  public void setUrlInfo(URLInfo urlInfo) { this.urlInfo = urlInfo; }
  
  public String[] getExtractEntityInfo() { return extractEntityInfo; }
  public void setExtractEntityInfo(String[] extractEntityInfo) {
    this.extractEntityInfo = extractEntityInfo;
  }
  
  public void withExtractEntityInfo(List<ExtractEntity> entities) {
    if(entities == null) return;
    
    extractEntityInfo = new String[entities.size()];
    for(int i = 0; i < entities.size(); i++) {
      StringBuilder b = new StringBuilder();
      ExtractEntity entity = entities.get(i);
      String name = entity.name();
      b.append(name).append("[");
      boolean first = true;
      for(String field : entity.keySet()) {
        if("name".equals(field)) continue;
        if("type".equals(field)) continue;
        if(!first) b.append(",");
        b.append(field);
        first = false;
      }
      b.append("]");
      extractEntityInfo[i] = b.toString();
    }
  }
  
  
}
