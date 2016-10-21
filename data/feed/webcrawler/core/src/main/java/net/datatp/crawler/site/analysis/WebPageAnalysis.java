package net.datatp.crawler.site.analysis;

import java.util.List;

import net.datatp.crawler.site.WebPageType;
import net.datatp.xhtml.extract.ExtractEntity;

public class WebPageAnalysis {
  private WebPageType         webPageType;
  private List<ExtractEntity> entities;
  
  public WebPageType getWebPageType() { return webPageType; }
  public void setWebPageType(WebPageType webPageType) { this.webPageType = webPageType; }
  
  public List<ExtractEntity> getEntities() { return entities; }
  public void setEntities(List<ExtractEntity> entities) { this.entities = entities; }
}