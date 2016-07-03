package net.datatp.xhtml.dom.selector;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextLengthSelector implements Selector {
	private int minLength = 100000, maxLength ;

	public TextLengthSelector(int min, int max) {
		this.minLength = min ;
		this.maxLength = max ;
	}

	public boolean isSelected(TNode node) {
		String ntext = node.getNodeValue() ;
		if(StringUtil.isEmpty(ntext)) return false ;
		if(ntext.length() >= minLength && ntext.length() <= maxLength) return true ;
		return false;
	}
}