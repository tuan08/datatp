package net.datatp.crawler.site;

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

  
  public WebPageType analyze(String anchorText, String url) {
    for(WebPageTypePattern sel : this.pattern) {
      if(sel.matches(url)) return sel.getType();
    }
    if(hasDetailPatternConfig) return WebPageType.uncategorized;
    return WebPageType.detail;
  }
}
