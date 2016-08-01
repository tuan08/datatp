package net.datatp.xhtml.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPathTree {
  private List<XPath> xpaths;
  private Map<String, Integer> indices;
  
  public XPathTree(List<XPath> xpaths) {
    this.xpaths = xpaths;
    reindex();
  }
  
  void reindex() {
    indices = new HashMap<>();
    for(int i = 0; i < xpaths.size(); i++) {
      XPath xpath = xpaths.get(i);
      indices.put(xpath.getXPathWithIndex(), i);
    }
  }
  
  public XPath getXPath(String xpath) { 
    int index = indices.get(xpath);
    return xpaths.get(index); 
  }
  
  public List<XPath> getXPaths() { return xpaths; }
  
  public XPath[] getXPathAsArray() {
    return xpaths.toArray(new XPath[xpaths.size()]);
  }
  
  public XPathTree subTree(XPath xpath) { return subTree(xpath.getXPathWithIndex()); }
  
  public XPathTree subTree(String fromXPath) {
    int from = indices.get(fromXPath);
    List<XPath> holder = new ArrayList<>();
    for(int i = from; i < xpaths.size(); i++) {
      XPath sel = xpaths.get(i);
      if(sel.getXPathWithIndex().startsWith(fromXPath)) {
        holder.add(sel);
      } else {
        break;
      }
    }
    return new XPathTree(holder);
  }
  
  public XPath removeXPath(String xpath) {
    int index = indices.remove(xpath);
    XPath ret = xpaths.remove(index);
    reindex();
    return ret;
  }
  
  public XPath removeXPath(XPath xpath) {
    return removeXPath(xpath.getXPathWithIndex());
  }
  
  public int removeSubXPathTree(String xpath) {
    int from = indices.get(xpath);
    int to = from + 1;
    for(int i = from; i < xpaths.size(); i++) {
      XPath sel = xpaths.get(i);
      if(sel.getXPathWithIndex().startsWith(xpath)) {
        to++;
      }
    }
    List<XPath> newList = new ArrayList<>();
    newList.addAll(xpaths.subList(0, from));
    newList.addAll(xpaths.subList(to, xpaths.size()));
    xpaths = newList;
    reindex();
    return to - from;
  }
  
  public int removeSubXPathTree(XPath xpath) {
    return removeSubXPathTree(xpath.getXPathWithIndex());
  }
  
  public int removeXPathWithAttr(String name, String[] value, boolean descendant) {
    int removeCount = 0;
    int i = 0; 
    while(i < xpaths.size()) {
      XPath xpath = xpaths.get(i);
      if(xpath.hasAttr(name, value)) {
        //remove this xpath and it sub xpaths;
        xpaths.set(i++, null);
        removeCount++;
        String xpathAncestor = xpath.getXPathWithIndex();
        while(i < xpaths.size()) {
          XPath sel = xpaths.get(i);
          if(sel.getXPathWithIndex().startsWith(xpathAncestor)) {
            xpaths.set(i++, null);
            removeCount++;
          } else {
            break;
          }
        }
      } else {
        i++;
      }
    }
    List<XPath> newList = new ArrayList<>();
    for(int k = 0; k < xpaths.size(); k++) {
      XPath sel = xpaths.get(k);
      if(sel != null) newList.add(sel);
    }
    xpaths = newList;
    reindex();
    return removeCount;
  }
  
  public String getText() {
    StringBuilder b = new StringBuilder();
    for(XPath sel : this.getXPaths()) {
      if(!sel.isTextNode()) continue;
      if(b.length() > 0) b.append("\n");
      b.append(sel.getText());
    }
    return b.toString();
  }
  
  public List<XPath> select(XPathSelector[] selectors, int max) {
    List<XPath> holder = new ArrayList<>();
    for(int i = 0; i < xpaths.size(); i++) {
      XPath sel = xpaths.get(i);
      for(XPathSelector selector : selectors) {
        if(selector.select(sel)) {
          holder.add(sel);
          if(holder.size() == max) return holder;
          break;
        }
      }
    }
    return holder;
  }
}