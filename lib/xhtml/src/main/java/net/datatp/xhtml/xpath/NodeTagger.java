package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

abstract public class NodeTagger implements NodeVisitor {

  public String getName() { return getClass().getSimpleName(); }
  
  @Override
  public void head(Node node, int depth) {
    tag(node, depth);
  }

  @Override
  public void tail(Node node, int depth) {
  }

  abstract public void tag(Node node, int deptd) ;
}
