package net.datatp.xhtml.dom.extract;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.tagger.ContentListBlockTagger;
import net.datatp.xhtml.dom.tagger.Tagger;
import net.datatp.xhtml.dom.tagger.TextBlockTagger;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MainContentExtractor extends ContentExtractor {
	private String mainContentTag ;
	private Tagger textBlockTagger ;
	private Tagger listBlockTagger ;
	
	public MainContentExtractor(String tag) {
		this.mainContentTag = tag ;
		textBlockTagger = new TextBlockTagger() ;
		listBlockTagger = new ContentListBlockTagger() ;
	}

	protected ExtractContent extractDetail(TDocument tdoc) {
		TNode[] textNodes = textBlockTagger.tag(tdoc, tdoc.getRoot()) ;
		if(textNodes.length > 0) {
			ExtractContent extractContent = new ExtractContent() ;
			TNode mainBlock = selectMainBlock(textNodes) ;
			extractContent.add(createDetail(mainContentTag, tdoc, mainBlock)) ;
			return extractContent;
		}
		return null ;
	}

	private TNode selectMainBlock(TNode[] nodes) {
		int maxTextSize = 0;
		int maxTextSizeIdx = 0 ;
		for(int i = 0; i < nodes.length; i++) {
			TNode mainBlock = findBlockWithTitle(nodes[i]) ;
			if(mainBlock != null) {
				return mainBlock ;
			}
			int textSize = nodes[i].getTextSize() ;
			if(textSize > maxTextSize) {
				maxTextSize = textSize;
				maxTextSizeIdx = i ;
			}
		}
		return nodes[maxTextSizeIdx] ;
	}
	
	protected ExtractContent extractList(TDocument tdoc) {
		TNode[] listNodes = listBlockTagger.tag(tdoc, tdoc.getRoot()) ;
		if(listNodes.length > 0) {
			StringBuilder contentB = new StringBuilder() ;
			for(int j = 0; j < listNodes.length; j++) {
				if(listNodes[j].hasAncestorTag(OTHER_JUNK_TEXT_TAG)) continue ;
				contentB.append(listNodes[j].getTextContent()).append("\n") ;
			}
			ExtractBlock block = new ExtractBlock("mainContent") ;
			block.setTitle(tdoc.getAnchorText(), null) ;
			block.setContent(contentB.toString(), null);
			ExtractContent extractContent = new ExtractContent();
			extractContent.add(block) ;
			block.setTags(mainContentTag, "content:list") ;
			return extractContent;
		}
		return null ;
	}
	
	protected ExtractContent extractOther(TDocument tdoc) {
		return extractOther(mainContentTag, tdoc) ;
	}
}