package net.datatp.xhtml.dom.selector;

import net.datatp.xhtml.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeSelector implements Selector {
	private String[] name ;
	
	public TNodeSelector(String ... name) {
		this.name = name ;
	}
	
  public boolean isSelected(TNode node) {
  	String nodeName = node.getNodeName() ;
  	if(name.length == 1) return name[0].equals(nodeName) ;
  	for(int i = 0; i < name.length; i++) {
  		if(name[i].equals(nodeName)) return true ;
  	}
  	return false ;
  }
}