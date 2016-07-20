package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Node;

public class XPathTagger extends NodeTagger {
  
  @Override
  public void tag(Node node, int deptd) {
    Node parent = node.parent();
    if(parent == null) {
      node.attr("xpath", node.nodeName() + "[" + node.siblingIndex() + "]") ;
    } else {
      String parentXpath = parent.attr("xpath");
      String xpath = parentXpath + "/" + node.nodeName() + "[" + node.siblingIndex() + "]";
      node.attr("xpath", xpath);
    }
  }
}
