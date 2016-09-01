package net.datatp.crawler.site;

import java.util.Comparator;
import java.util.regex.Pattern;

import net.datatp.util.text.StringUtil;

public class WebPageTypePattern {
  final static public Comparator<WebPageTypePattern> PRIORITY_COMPARATOR = new Comparator<WebPageTypePattern>() {
    @Override
    public int compare(WebPageTypePattern p1, WebPageTypePattern p2) {
      return p1.getType().compareTo(p2.getType());
    }
  };
  
  private WebPageType          type = WebPageType.uncategorized;
  private String[]             pattern;
  transient private Pattern[]  regexPattern;

  public WebPageTypePattern() {}
  
  public WebPageTypePattern(WebPageType type, String ... pattern) {
    this.type = type;
    this.pattern = pattern;
  }

  public WebPageType getType() { return type; }
  public void setType(WebPageType type) { this.type = type; }
  
  public String[] getPattern() { return pattern; }
  public void setPattern(String[] pattern) { this.pattern = pattern; }
  
  public String toString() { return type + ":" + StringUtil.joinStringArray(pattern) ; }

  public void compile() {
    regexPattern = new Pattern[pattern.length];
    for(int i = 0; i < pattern.length; i++) {
      regexPattern[i] = Pattern.compile(pattern[i]);
    }
  }
  
  public boolean matches(String url) {
    for(int i = 0; i < pattern.length; i++) {
      if(regexPattern[i].matcher(url).matches()) return true;
    }
    return false;
  }
}