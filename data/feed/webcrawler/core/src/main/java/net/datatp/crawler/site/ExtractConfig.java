package net.datatp.crawler.site;

public class ExtractConfig {
  static public enum MatchType   { url, title }
  static public enum ExtractAuto { content, article, comment, forum, product, job, classified }
  
  private String         name = "content";
  private Matcher        matcher;
  private ExtractAuto[]  extractAuto = { ExtractAuto.content };
  private XPathPattern[] extractXPath;

  public String getName() { return name; }
  public void setName(String name) { this.name = name;}
  
  public Matcher getMatcher() { return matcher; }
  public void setMatcher(Matcher matcher) { this.matcher = matcher; }
  
  public ExtractAuto[] getExtractAuto() { return extractAuto; }
  public void setExtractAuto(ExtractAuto ... extractAuto) { this.extractAuto = extractAuto; }
  
  public XPathPattern[] getExtractXPath() { return extractXPath; }
  public void setExtractXPath(XPathPattern[] extractXPath) { this.extractXPath = extractXPath; }
  
  static public class Matcher {
    private MatchType type;
    private String[]  pattern;

    public MatchType getType() { return type; }
    public void setType(MatchType type) { this.type = type; }
    
    public String[] getPattern() { return pattern; }
    public void setPattern(String[] pattern) { this.pattern = pattern;}
  }
  
  static public class XPathPattern {
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
    config.setExtractAuto(ExtractAuto.article, ExtractAuto.comment);
    return config;
  }
  
  static public ExtractConfig forum() {
    ExtractConfig config  = new ExtractConfig();
    config.setName("forum");
    config.setExtractAuto(ExtractAuto.forum);
    return config;
  }
}
