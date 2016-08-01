package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import net.datatp.util.text.StringUtil;

public class XPath {
  static public enum Section { Head, Body, Unknown }
  
  private String     xpathWithIndex;
  private String     xpathWithAncestorIndex;
  private Node       node;
  private XPathInfo  info;
  private Section    section = Section.Unknown;
  private String     normalizeText;
  Fragment[]         fragment;
  
  public XPath(String xpathWithIndex, Node node) {
    this.xpathWithIndex = xpathWithIndex;
    this.xpathWithAncestorIndex = xpathWithIndex.substring(0, xpathWithIndex.lastIndexOf('['));
    this.node  = node;
    
    String[] fragmentExp = xpathWithIndex.split("/");
    fragment = new Fragment[fragmentExp.length];
    for(int i = 0; i < fragmentExp.length; i++) {
      String exp = fragmentExp[i];
      int idx = exp.indexOf('[');
      String name  = exp.substring(0, idx);
      int    index = Integer.parseInt(exp.substring(idx + 1, exp.length() - 1));
      fragment[i] = new Fragment(name, index);
      if("head".equals(name)) section = Section.Head;
      else if("body".equals(name)) section = Section.Body;
    }
  }
  
  public String getXPathWithIndex() { return xpathWithIndex; }
  
  public String getXPathWithAncestorIndex() { return xpathWithAncestorIndex; }
  
  public Node getNode() { return node; }
  
  public int getDepth() { return fragment.length; }
  
  public String getText() {
    if(node instanceof TextNode) return ((TextNode)node).text();
    else if(node instanceof Element) return ((Element)node).text();
    return null;
  }
  
  public String getNormalizeText() {
    if(normalizeText == null) normalizeText = getText().trim().toLowerCase();
    return normalizeText;
  }
  
  public boolean hasAttr(String name) { return node.hasAttr(name); }
  
  public boolean hasAttr(String name, String expectVal) { 
    String val = node.attr(name);
    if(val == null) return false;
    return val.equals(expectVal);
  }
  
  public boolean hasAttr(String name, String ... expectVal) { 
    String val = node.attr(name);
    if(val == null) return false;
    return StringUtil.isIn(val, expectVal);
  }
  
  public boolean isTextNode() { return node instanceof TextNode ; }
  
  public Section getSection() { return this.section; }
  
  public XPathInfo getXPathInfo() {
    if(info == null) {
      info = new XPathInfo();
      node.traverse(info);
    }
    return info;
  }
  
  public String getXPathWithoutIndex() {
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < fragment.length; i++) {
      if(i > 0) b.append("/");
      b.append(fragment[i].getName());
    }
    return b.toString();
  }
  
  public String findClosestAncestor(XPath other) {
    int limit = fragment.length ;
    if(other.fragment.length < limit) limit = other.fragment.length ;
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < limit; i++) {
      if(fragment[i].name.equals(other.fragment[i].name) && fragment[i].index == other.fragment[i].index) {
        if(b.length() > 0) b.append("/");
        b.append(fragment[i].name).append('[').append(fragment[i].index).append(']');
      } else {
        break ;
      }
    }
    return b.toString();
  }
  
  public String getAncestor(int backLevel) {
    int limit = fragment.length - backLevel;
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < limit; i++) {
      if(b.length() > 0) b.append("/");
      b.append(fragment[i].name).append('[').append(fragment[i].index).append(']');
    }
    return b.toString();
  }
  
  static public class Fragment {
    String name;
    int    index;
    
    Fragment(String name, int index) {
      this.name  = name;
      this.index = index;
    }

    public String getName() { return name; }

    public int getIndex() { return index; }
    
    public boolean equals(Fragment other) {
      return name.equals(other.name) && index == other.index;
    }
    
    public boolean equalsIgnoreIndex(Fragment other) {
      return name.equals(other.name);
    }
    
    public String toString() { return name + "[" + index + "]"; }
  }
}
