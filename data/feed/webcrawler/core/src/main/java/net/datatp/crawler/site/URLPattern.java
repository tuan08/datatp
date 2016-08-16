package net.datatp.crawler.site;

import java.util.Comparator;

public class URLPattern {
  static public enum Type { ignore, detail, list, unknown }

  final static public Comparator<URLPattern> PRIORITY_COMPARATOR = new Comparator<URLPattern>() {
    @Override
    public int compare(URLPattern p1, URLPattern p2) {
      return p1.getType().compareTo(p2.getType());
    }
  };
  
  private Type     type = Type.unknown ;
  private String[] pattern;
  
  public URLPattern() {}
  
  public URLPattern(Type type, String ... pattern) {
    this.type = type;
    this.pattern = pattern;
  }

  public Type getType() { return type; }
  public void setType(Type type) { this.type = type; }
  
  public String[] getPattern() { return pattern; }
  public void setPattern(String[] pattern) { this.pattern = pattern; }
}