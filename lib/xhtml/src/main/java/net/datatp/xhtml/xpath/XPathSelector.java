package net.datatp.xhtml.xpath;

import net.datatp.util.text.matcher.StringMatcher;

public interface XPathSelector {
  public boolean select(XPath xpath);
  
  static public class TextNodeSelector implements XPathSelector {
    private StringMatcher matcher;
    
    public TextNodeSelector(StringMatcher matcher) {
      this.matcher = matcher;
    }
    
    @Override
    public boolean select(XPath xpath) {
      if(!xpath.isTextNode()) return false;
      String text = xpath.getNormalizeText();
      return matcher.matches(text) ;
    }
    
  }
  
  static public class LinkNodeSelector implements XPathSelector {
    private StringMatcher matcher;
    
    public LinkNodeSelector(StringMatcher matcher) {
      this.matcher = matcher;
    }
    
    @Override
    public boolean select(XPath xpath) {
      if("a".equals(xpath.getNode().nodeName())) {
        String url = xpath.getNode().attr("href");
        if(url == null) return false;
        return matcher.matches(url);
      }
      return false;
    }
  }
  
  static public class AttrNodeSelector implements XPathSelector {
    private String        name;
    private StringMatcher matcher;
    
    public AttrNodeSelector(String name, StringMatcher matcher) {
      this.name   = name;
      this.matcher = matcher;
    }
    
    @Override
    public boolean select(XPath xpath) {
      String value = xpath.getNode().attr(name);
      if(value == null) return false;
      return matcher.matches(value);
    }
  }
}
