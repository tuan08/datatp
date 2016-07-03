package net.datatp.xhtml.dom.extract;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroup;
import net.datatp.xhtml.dom.selector.CssClassSelector;
import net.datatp.xhtml.dom.selector.ElementIdSelector;
import net.datatp.xhtml.dom.selector.OrSelector;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TextSimilaritySelector;
import net.datatp.xhtml.dom.selector.URLPatternSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ForumContentExtractor extends ContentExtractor {
	final static public String[] KEYWORD_LABELS = {
		"Tham gia", "Bài gửi", "Cảm ơn", "Thành viên", "Registered User", "Bài viết",
		"Được cảm ơn", "Trả lời kèm trích dẫn", "Tham gia từ", "Trạng thái", "Join Date:", 
		"Posts:"
	} ;

	final static String[] ELEMENT_PATTERN = { "*postbody*", "*postcontent*" } ;
	
	final static String[] LINK_PATTERN = { "*newreply*", "*addpost*" } ;
	
	private String   mainContentTag ;
	private Selector candidateNodeSelector;
	
	public ForumContentExtractor(String tag) {
		this.mainContentTag = tag ;
		TextSimilaritySelector textSelector = new TextSimilaritySelector(KEYWORD_LABELS) ;
		URLPatternSelector urlSelector      = new URLPatternSelector(LINK_PATTERN) ;
		CssClassSelector cssSelector        = new CssClassSelector(ELEMENT_PATTERN) ;
		ElementIdSelector idSelector        = new ElementIdSelector(ELEMENT_PATTERN) ;
		candidateNodeSelector = 
			new OrSelector(textSelector, urlSelector, cssSelector, idSelector) ;
	}

	protected ExtractContent extractDetail(TDocument tdoc) {
		TNode[] nodes = tdoc.getRoot().select(candidateNodeSelector, false) ;
		TNodeGroup[] groups = TNodeGroup.groupBySimilarTNode(nodes, 5) ;
		groups = TNodeGroup.filterTNodeGroupByOccurence(groups, 2) ;
		TNodeGroup[] candidateGroups = TNodeGroup.findRepeatTNodeGroup(groups) ;
		if(candidateGroups == null || candidateGroups.length == 0) {
			candidateGroups = groups ;
		}
		//TNodeGroup.dump(System.out, groups) ;
		TNode mainBlock = TNodeGroup.getCommonAncestor(candidateGroups) ;
		if(mainBlock != null) {
			ExtractContent extractContent = new ExtractContent() ;
			extractContent.add(createDetail(mainContentTag, tdoc, mainBlock)) ;
			return extractContent;
		}
		return null ;
	}

	protected ExtractContent extractList(TDocument tdoc) {
		return null ;
	}

	protected ExtractContent extractOther(TDocument tdoc) {
		return extractOther(mainContentTag, tdoc) ;
	}
}