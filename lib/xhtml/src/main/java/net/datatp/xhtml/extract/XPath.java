package net.datatp.xhtml.extract;

import org.jsoup.nodes.Element;

public class XPath {
  private Fragment[] fragment;
  
  public XPath(Element ele) {
  }
  
  public XPath(XPath parent, Element ele) {
    fragment = new Fragment[parent.fragment.length + 1];
    System.arraycopy(parent.fragment, 0, fragment, 0, parent.fragment.length);
    fragment[parent.fragment.length] = new Fragment(ele.nodeName(), 0);
  }
  
  
  static public class Fragment {
    private String name;
    private int    index;
    
    Fragment(String name, int index) {
      this.name  = name;
      this.index = index;
    }

    public String getName() { return name; }

    public int getIndex() { return index; }
  }
}