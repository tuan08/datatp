package net.datatp.xhtml.visitor;

import org.w3c.dom.Node;

import net.datatp.util.text.StringUtil;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class NodeDeleteVisitor extends NodeVisitor {
  private String[] nodeName ;
	
	public NodeDeleteVisitor() {
	}
	
	public NodeDeleteVisitor(String[] nodeName) {
		this.nodeName = nodeName ;
	}
	
	public void preTraverse(Node node) {}
  
  public void postTraverse(Node node) {}
  
  final public void traverse(Node node) {
    Node child = node.getFirstChild();
    Node nextChild = null ;
    while (child != null) {
      nextChild = child.getNextSibling() ;
      if(shouldDelete(child)) {
        Node parent = child.getParentNode() ;
        parent.removeChild(child) ;
      } else {
        traverse(child);
      }
      child = nextChild ;
    }
  }
  
  protected boolean shouldDelete(Node node) {
  	if(nodeName == null) return false ;
  	String name = node.getLocalName() ;
  	return StringUtil.isIn(name, nodeName) ;
  }
}