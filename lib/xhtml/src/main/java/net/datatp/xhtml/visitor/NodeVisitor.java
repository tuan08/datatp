package net.datatp.xhtml.visitor;

import org.w3c.dom.Node;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 23, 2010  
 */
abstract public class NodeVisitor {
  public void traverse(Node node) {
    preTraverse(node) ;
    Node child = node.getFirstChild();
    while (child != null) {
      traverse(child);
      child = child.getNextSibling();
    }
    postTraverse(node) ;
  }
  
  abstract public void preTraverse(Node node) ;
  abstract public void postTraverse(Node node) ;
}
