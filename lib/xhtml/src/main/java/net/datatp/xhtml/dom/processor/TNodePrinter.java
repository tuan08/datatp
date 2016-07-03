package net.datatp.xhtml.dom.processor;

import java.io.PrintStream;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.TNode;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class TNodePrinter extends TNodeProcessor {
  private PrintStream out ;
  private int level = 0;
  private boolean printTag = true ;
  private boolean printXpath = true ;
  private boolean cssClass = true; 
  
  public TNodePrinter(PrintStream out) {
    this.out = out ;
  }
  
  public TNodePrinter setPrintTag(boolean b) {
  	printTag = b ;
  	return this ;
  }
  
  public TNodePrinter setXPath(boolean b) {
  	printXpath = b ;
  	return this ;
  }
  
  public TNodePrinter setCssClass(boolean b) {
  	cssClass = b ;
  	return this ;
  }
  public void preTraverse(TNode node) {
    if(level > 0) {
      for(int i = 0; i < level; i++) out.print("  ") ;
    }
    String text = node.getNodeValue() ;
    out.print(node.getNodeName() + "[" + getDescription(node) + "] " + node.getLinkNodeDensity() + ":" + node.getLinkTextDensity());
    out.println();
    if(text != null) {
    	if(level > 0) {
    		for(int i = 0; i < level; i++) out.print("  ") ;
    	}
    	out.print("  " + text);
    	out.println();
    }
    level++ ;
  }
  
  private String getDescription(TNode node) {
  	StringBuilder b = new StringBuilder() ;
  	if(printTag && node.getTags() != null) {
  		b.append("tag = ").append(StringUtil.join(node.getTags(), ",")) ;
  	}
  	
  	if(cssClass && node.getCssClass() != null) {
  		if(b.length() > 0) b.append(" | ") ;
  		b.append("cssclass = ").append(node.getCssClass()) ;
  	}
  	
  	if(printXpath) {
  		if(b.length() > 0) b.append(" | ") ;
  		b.append("xpathNode = ").append(node.getXPath().toString()) ;
  	}
  	return b.toString() ;
  }
  
  public void postTraverse(TNode node) {
    level-- ;
  }
}