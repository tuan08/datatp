package net.datatp.crawler.site;

import java.io.Serializable;

public class ExtractConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  static public enum MatchType   { any, url, title }
  static public enum ExtractType { content, article, comment, forum, product, job, classified }
  
  private String         name        = "content";
  private ExtractType    extractType = ExtractType.content;
  private MatchType      type        = MatchType.any;
  private String[]       pattern;
  private XPathPattern[] extractXPath;

  public ExtractConfig() { }

  public ExtractConfig(String name, ExtractType extractType) {
    this.name        = name;
    this.extractType = extractType;
  }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public ExtractType getExtractType() { return extractType; }
  public void setExtractType(ExtractType extractAuto) { this.extractType = extractAuto; }
  
  public MatchType getMatchType() { return type; }
  public void setMatchType(MatchType type) { this.type = type; }
  
  public String[] getMatchPattern() { return pattern; }
  public void setMatchPattern(String[] pattern) { this.pattern = pattern;}

  public XPathPattern[] getExtractXPath() { return extractXPath; }
  public void setExtractXPath(XPathPattern ... extractXPath) { this.extractXPath = extractXPath; }
  
  static public class XPathPattern implements Serializable {
    private static final long serialVersionUID = 1L;

    private String   name;
    private String[] xpath;
    
    public XPathPattern() {}
    
    public XPathPattern(String name, String ... xpath) {
      this.name  = name;
      this.xpath = xpath;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String[] getXpath() { return xpath; }
    public void setXpath(String[] xpath) { this.xpath = xpath; }
  }
  
  static public ExtractConfig article() { return new ExtractConfig("content", ExtractType.article); }
  
  static public ExtractConfig forum() { return new ExtractConfig("content", ExtractType.forum); }
}
