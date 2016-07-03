package net.datatp.xhtml.visitor;

import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class NodePrintVisitor extends NodeVisitor {
  private PrintStream out ;
  private int level = 0;
  
  public NodePrintVisitor(PrintStream out) {
    this.out = out ;
  }
  
  public void preTraverse(Node node) {
    if(level > 0) {
      for(int i = 0; i < level; i++) out.print("  ") ;
    }
    out.print(node.getNodeName() + "[" + getText(node) + "]");
    out.println();
    level++ ;
  }
  
  private String getText(Node node) {
  	String value = node.getNodeValue() ;
  	if(value == null) return "" ;
  	return value.trim() ;
  }
  
  public void postTraverse(Node node) {
    level-- ;
  }
}