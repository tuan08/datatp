package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Node;

public class XPathTagger extends NodeTagger {
  final static public String XPATH_ATTR = "xpath";
  
  @Override
  public void tag(Node node, int depth) {
    Node parent = node.parent();
    if(parent == null) {
      node.attr(XPATH_ATTR, node.nodeName() + "[" + node.siblingIndex() + "]") ;
    } else {
      String parentXpath = parent.attr(XPATH_ATTR);
      String xpath = parentXpath + "/" + node.nodeName() + "[" + node.siblingIndex() + "]";
      node.attr(XPATH_ATTR, xpath);
    }
  }
}
