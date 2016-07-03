package net.datatp.xhtml.dom.processor;

import java.util.List;

import net.datatp.xhtml.dom.TNode;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 23, 2010  
 */
public class TNodeProcessor {
	public void process(TNode node) {
		traverse(node) ;
	}
	
  protected void traverse(TNode node) {
    preTraverse(node) ;
    List<TNode> children = node.getChildren() ;
    if(children != null) {
    	for(int i = 0 ; i < children.size(); i++) {
    		TNode child = children.get(i) ;
    		traverse(child) ;
    	}
    }
    postTraverse(node) ;
  }
  
  protected void preTraverse(TNode node) { }  
  protected void postTraverse(TNode node) { }
}
