package net.datatp.xhtml.extract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class XpathConfig implements Serializable {
  private String name ;
  private String urlPattern ;
  private Map<String, String> xpath ;

  transient private Pattern compiledUrlPattern ;

  public String getName() { return name ;}
  public void setName(String name) { this.name = name ; }

  public String getUrlPattern() { return urlPattern ;}
  public void setUrlPattern(String pattern) { urlPattern = pattern ; }

  public void addXpath(String name, String xpath) {
    if(this.xpath == null) this.xpath = new HashMap<String, String>() ;
    this.xpath.put(name, xpath) ;
  }

  public Map<String, String> getXpath() { return this.xpath ; }
  public void setXpath(Map<String, String> xpaths) { this.xpath = xpaths ; }

  public boolean acceptUrl(String url) {
    if(urlPattern == null || urlPattern.length() == 0) return true ;
    if(compiledUrlPattern == null) {
      compiledUrlPattern = Pattern.compile(urlPattern) ;
    }
    return compiledUrlPattern.matcher(url).matches() ;
  }
}
