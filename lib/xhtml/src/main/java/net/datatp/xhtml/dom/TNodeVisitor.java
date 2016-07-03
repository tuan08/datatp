package net.datatp.xhtml.dom;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TNodeVisitor {
	final static public int CONTINUE = 0 ;
	final static public int SKIP     = 1 ;
	
	public int onVisit(TNode node) ;
}
