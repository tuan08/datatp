package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.WebPageType;
import net.datatp.crawler.site.WebPageTypePattern;
import net.datatp.util.URLInfo;

public class WebPageTypeAnalyzer {
  private WebPageTypePattern[] pattern;
  private boolean hasDetailPatternConfig = false;
  
  public WebPageTypeAnalyzer(WebPageTypePattern[] pattern) {
    this.pattern = pattern;
    if(this.pattern == null) this.pattern = new WebPageTypePattern[0];
    for(WebPageTypePattern sel : this.pattern) {
      if(sel.getType() == WebPageType.detail) {
        hasDetailPatternConfig = true;
      }
      sel.compile();
    }
  }
  
  public boolean hasDetailPatternConfig() { return this.hasDetailPatternConfig; }
  
  public boolean isIgnore(String anchorText, String url) {
    for(int i = 0; i < pattern.length; i++) {
      if(pattern[i].getType() != WebPageType.ignore) continue;
      if(pattern[i].matches(url)) return true;
    }
    return false;
  }

  
  public WebPageType analyze(String anchorText, URLInfo urlInfo) {
    for(WebPageTypePattern sel : this.pattern) {
      if(sel.matches(urlInfo.getPathWithParams())) return sel.getType();
    }
    return null;
  }
}
