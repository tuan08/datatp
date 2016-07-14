package net.datatp.webcrawler.analysis;

import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface Analyzer {
	public void analyze(XhtmlDocument xDoc, TDocument tdoc) ;
}
