package net.datatp.xhtml.dom.extract;

import java.util.Iterator;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.extract.DocumentExtractor.Type;
import net.datatp.xhtml.extract.XpathConfig;
import net.datatp.xhtml.parser.JSoupParser;

public class DocumentXPathExtractor {
	private String[] cleanTag = {"object", "script"} ;
	
	public void setCleanTag(String[] tag) { this.cleanTag = tag ; }
	
	public ExtractContent extract(Type type, TDocument tdoc, XpathConfig[] xpathConfig) {
		if(xpathConfig == null) return null ;
		ExtractContent extractContent = null ;
		for(XpathConfig sel : xpathConfig) {
			//System.out.println("Accept url: " + sel.acceptUrl(tdoc.getUrl())) ;
			if(sel.acceptUrl(tdoc.getUrl())) {
				//System.out.println("  xpath config: " + sel.getName()) ;
				Document doc = JSoupParser.INSTANCE.parse(tdoc.getXHTML()) ;
				ExtractBlock block = new ExtractBlock(sel.getName()) ;

				Map<String, String> xpaths = sel.getXpath() ;
				Iterator<Map.Entry<String, String>> i = xpaths.entrySet().iterator() ;
				while(i.hasNext()) {
					Map.Entry<String, String> entry = i.next() ;
					Elements founds = extract(doc, entry.getValue()) ;
					//System.out.println("Xpath " + entry.getValue() + ": " + founds);
					if(founds != null) {
						cleanTag(founds) ;
						block.add(entry.getKey(), founds.html(), founds) ;
						founds.remove() ;
					}
				}
				if(block.countExtractNode() == 0) continue ;
				if(extractContent == null) extractContent = new ExtractContent() ;
				extractContent.add(block) ;
			}
		}
		return extractContent ;
	}
	
	private Elements extract(Document doc, String xpath) {
		if(xpath == null || xpath.length() == 0) return null ;
		xpath = xpath.replace("/", " > ") ;
		Elements founds = doc.select(xpath);
		return founds;
	}
	
	private void cleanTag(Elements elements) {
		for(String selTag : cleanTag) {
			Elements founds = elements.select(selTag) ;
			founds.remove() ;
		}
	}
}