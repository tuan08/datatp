package net.datatp.xhtml.dom.tagger;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroupByXPath;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TNodeSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class LinkBlockTagger2 extends Tagger {
	final static public String BLOCK_LINK_ACTION = "block:link:action" ;
	final static public String BLOCK_LINK_RELATED   = "block:link:related" ;
	
	private Selector selector = new TNodeSelector("a") ;
	
	public TNode[] tag(TDocument tdoc, TNode node) {
		TNodeGroupByXPath[] groups = doSelect(node, selector, 3) ;
		TNode[] tagNodes = tag(node, groups) ;
	  //dump(groups) ;
		return tagNodes ;
	}
	
	private TNode[] tag(TNode root, TNodeGroupByXPath[] groups) {
		List<TNode> holder = new ArrayList<TNode>() ;
		for(TNodeGroupByXPath group : groups) {
			List<TNode> nodes = group.getTNodes() ;
			if(nodes.size() < 2) continue ;
			TNode ancestor = nodes.get(0).getAncestor(group.xpathNode) ;
			int textSize = ancestor.getTextSize() ;
			if(textSize < 1) continue ;
			int linkTextSize = 0 ;
			for(int i = 0; i < nodes.size(); i++) {
				linkTextSize += nodes.get(i).getTextSize() ;
			}
			float ratio = (float)linkTextSize/textSize ;
			if(ratio < 0.7) continue ; 
			int avgTextSize = linkTextSize/nodes.size() ;
			if(avgTextSize < 15) {
				ancestor.addTag(BLOCK_LINK_ACTION) ;
			} else {
				ancestor.addTag(BLOCK_LINK_RELATED) ;
			}
			holder.add(ancestor) ;
		}
		return holder.toArray(new TNode[holder.size()]) ;
	}
}