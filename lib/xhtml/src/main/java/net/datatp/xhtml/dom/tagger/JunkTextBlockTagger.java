package net.datatp.xhtml.dom.tagger;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.selector.CssClassSelector;
import net.datatp.xhtml.dom.selector.ElementIdSelector;
import net.datatp.xhtml.dom.selector.OrSelector;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TNodeSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JunkTextBlockTagger extends Tagger {
	final static public String BLOCK_TEXT_JUNK = "block:text:junk" ;
	
	final static public String[] PATTERN = {
		"*navigation*", "*nav-*", "*nav_*",  "*-nav*", "*_nav*", 
		"*menu*", "*breadcumbs*", "*sidebar*",
		"*banner*", "*footer*" 
	};

	final static public String[] JUNK_TEXT_NODE_NAME = {
		"head", "meta", "input", "select", "textarea", "button"
	};
	
	private Selector selector ;
	
	public JunkTextBlockTagger() {
		Selector cssSelector = new CssClassSelector(PATTERN) ;
		Selector idSelector  = new ElementIdSelector(PATTERN) ;
		Selector nodeNameSelector  = new TNodeSelector(JUNK_TEXT_NODE_NAME) ;
		selector = new OrSelector(cssSelector, idSelector, nodeNameSelector) ;
	}
	
	public TNode[] tag(TDocument tdoc, TNode node) {
		TNode[] nodes = node.select(selector) ;
		for(TNode sel : nodes) {
			sel.addTag(BLOCK_TEXT_JUNK) ;
		}
		return nodes ;
	}
}