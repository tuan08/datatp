package net.datatp.xhtml.xpath;

import java.util.SortedMap;
import java.util.TreeMap;

public class XPathTree {
  private SortedMap<String, XPath> xpaths;
  private XPath[] flatXpaths;
  
  public XPathTree(SortedMap<String, XPath> xpaths) {
    this.xpaths = xpaths;
  }
  
  public XPath getXPath(String xpath) { return xpaths.get(xpath); }
  
  public XPath[] getFlatXPaths() {
    if(flatXpaths == null) {
      flatXpaths = xpaths.values().toArray(new XPath[xpaths.size()]);
    }
    return flatXpaths;
  }
  
  
  public XPathTree subTree(XPath xpath) {
    //copy the map for safe modification
    TreeMap<String, XPath> copyMap = new TreeMap<>(subSortedMap(xpath.getXPathWithIndex()));
    return new XPathTree(copyMap);
  }
  
  public XPath removeXPath(String xpath) {
    flatXpaths = null;
    return xpaths.remove(xpath);
  }
  
  public XPath removeXPath(XPath xpath) {
    flatXpaths = null;
    return xpaths.remove(xpath.getXPathWithIndex());
  }
  
  public int removeSubXPathTree(String xpath) {
    flatXpaths = null;
    String[] keys = subSortedKeys(xpath);
    for(String key : keys) {
      XPath removedXPath = xpaths.remove(key);
      System.out.println("  removeSubXPath: " + removedXPath.getText());
    }
    return keys.length;
  }
  
  public int removeSubXPathTree(XPath xpath) {
    return removeSubXPathTree(xpath.getXPathWithIndex());
  }
  
  public int removeXPathWithAttr(String name, String[] value, boolean descendant) {
    int removeCount = 0;
    for(XPath xpath : getFlatXPaths()) {
      if(xpath.hasAttr(name, value)) {
        System.out.println("find link xpath " + xpath.getXPathWithIndex());
        if(descendant) {
          removeCount += removeSubXPathTree(xpath.getXPathWithIndex());
        } else {
          removeXPath(xpath);
          removeCount++;
        }
      }
    }
    flatXpaths = null;
    return removeCount;
  }
  
  SortedMap<String, XPath> subSortedMap(String xpath) {
    String fromXpath = xpath;
    String toXpath   = fromXpath + "/zzzzzzz";
    return xpaths.subMap(fromXpath, toXpath);
  }
  
  String[] subSortedKeys(String xpath) {
    SortedMap<String, XPath> map = subSortedMap(xpath);
    return map.keySet().toArray(new String[map.size()]);
  }
}