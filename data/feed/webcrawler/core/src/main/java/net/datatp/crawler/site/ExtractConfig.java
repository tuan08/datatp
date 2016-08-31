package net.datatp.crawler.site;

import java.io.Serializable;

public class ExtractConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  static public enum MatchType   { any, url, title }
  static public enum ExtractAuto { none, content, article, comment, forum, product, job, classified }
  
  private String         name        = "content";
  private MatchType      type;
  private String[]       pattern;
  private XPathPattern[] extractXPath;
  private ExtractAuto    extractAuto =  ExtractAuto.none;

  public String getName() { return name; }
  public void setName(String name) { this.name = name;}
  
  public MatchType getMatchType() { return type; }
  public void setMatchType(MatchType type) { this.type = type; }
  
  public String[] getMatchPattern() { return pattern; }
  public void setMatchPattern(String[] pattern) { this.pattern = pattern;}

  public XPathPattern[] getExtractXPath() { return extractXPath; }
  public void setExtractXPath(XPathPattern[] extractXPath) { this.extractXPath = extractXPath; }
  
  public ExtractAuto getExtractAuto() { return extractAuto; }
  public void setExtractAuto(ExtractAuto extractAuto) { this.extractAuto = extractAuto; }
  
  static public class XPathPattern implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String xpath;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getXpath() { return xpath; }
    public void setXpath(String xpath) { this.xpath = xpath; }
  }
  
  static public ExtractConfig article() {
    ExtractConfig config  = new ExtractConfig();
    config.setName("article");
    config.setExtractAuto(ExtractAuto.article);
    return config;
  }
  
  static public ExtractConfig forum() {
    ExtractConfig config  = new ExtractConfig();
    config.setName("forum");
    config.setExtractAuto(ExtractAuto.forum);
    return config;
  }
}
