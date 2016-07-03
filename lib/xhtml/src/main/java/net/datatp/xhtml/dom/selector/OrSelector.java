package net.datatp.xhtml.dom.selector;

import net.datatp.xhtml.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class OrSelector implements Selector {
	private Selector[] selectors ;
	
	public OrSelector(Selector ...  selector) {
		this.selectors = selector ;
	}
	
  public boolean isSelected(TNode node) {
  	for(Selector sel : selectors) {
  		if(sel.isSelected(node)) return true ;
  	}
  	return false;
  }
}
