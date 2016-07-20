package net.datatp.xhtml.xpath;

import java.util.List;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class NodeCleanerVisitor implements NodeVisitor {
  private NodeCleaner[] cleaners;
  
  public NodeCleanerVisitor(NodeCleaner ...cleaner) {
    this.cleaners = cleaner;
  }
  
  @Override
  public void head(Node node, int depth) {
    List<Node> children = node.siblingNodes();
    for(int i = 0; i < children.size(); i++) {
      Node child = children.get(i);
      for(NodeCleaner cleaner : cleaners) {
        if(!cleaner.keep(child)) {
          child.remove();
          break;
        }
      }
    }
  }
  
  @Override
  public void tail(Node node, int depth) {
  }
}
