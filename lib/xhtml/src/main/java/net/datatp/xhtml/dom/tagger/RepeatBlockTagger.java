package net.datatp.xhtml.dom.tagger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroup;
import net.datatp.xhtml.dom.TNodeXPath;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TNodeSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RepeatBlockTagger extends Tagger {
	final static public String   BLOCK_REPEAT_ITEM    = "block:repeat:item" ;
	final static public String   BLOCK_REPEAT         = "block:repeat" ;
	
	static  Selector CANDIDATE_SELECTOR = new Selector() {
    public boolean isSelected(TNode node) {
    	return node.getCssClass() != null ;
    }
	};
	
	public TNode[] tag(TDocument tdoc, TNode root) {
		TNodeGroup[] groups = findTNodeGroupCandidates(root);
		System.out.println("Found Groups: " + groups.length);
		
		List<RepeatTNodeGroup> repeatGroups = new ArrayList<RepeatTNodeGroup>() ;
		for(int i = 0; i < groups.length; i++) {
			boolean foundRepeatGroup = false ;
			for(int j = 0; j < repeatGroups.size(); j++) {
				RepeatTNodeGroup repeatGroup = repeatGroups.get(j) ;
				float similarity = repeatGroup.similarity(groups[i]) ;
				if(similarity >= 0.75) {
					repeatGroup.add(groups[i]) ;
					foundRepeatGroup = true ;
					break ;
				}
			}
			if(!foundRepeatGroup) {
				RepeatTNodeGroup repeatGroup = new RepeatTNodeGroup() ;
				repeatGroup.add(groups[i]) ;
				repeatGroups.add(repeatGroup) ;
			}
		}
		
		System.out.println("Found Repeat Group Candidate: " + repeatGroups.size());
		
		List<TNode> tagNodes = new ArrayList<TNode>() ;
		for(int i = 0; i < repeatGroups.size(); i++) {
			RepeatTNodeGroup repeatGroup = repeatGroups.get(i) ;
			if(repeatGroup.holder.size() < 5) {
				//System.out.println("REMOVE: (" + repeatGroup.holder.size() + ")" +  repeatGroup.getCommonAncestor()) ;
				//repeatGroup.holder.get(0).dumpPath(System.out) ;
				continue ;
			}
			TNode ancestor = repeatGroup.getCommonAncestor() ;
			ancestor.addTag(BLOCK_REPEAT) ;
			for(int j = 0; j < repeatGroup.holder.size(); j++) {
				TNode node = repeatGroup.holder.get(j).getCommonAncestor() ;
				node.addTag(BLOCK_REPEAT_ITEM) ;
				//tagNodes.add(node) ;
			}
			tagNodes.add(ancestor) ;
		}
		System.out.println("Found Repeat Group: " + tagNodes.size());
		return tagNodes.toArray(new TNode[tagNodes.size()])  ;
	}

	private TNodeGroup[] findTNodeGroupCandidates(TNode root) {
		Selector selector = new TNodeSelector("a") ;
		TNode[] nodes =  root.select(selector) ;
		Map<String, TNodeGroup> candidates = new LinkedHashMap<String, TNodeGroup>() ;
		for(TNode sel : nodes) {
			if(sel.getTextSize() == 0) continue ;
			TNodeGroup group = new TNodeGroup(sel.getParent(), CANDIDATE_SELECTOR) ;
			if(group.getTNodes().size() < 2) {
				group = new TNodeGroup(sel.getParent().getParent(), CANDIDATE_SELECTOR) ;
			}
			if(group.getTNodes().size() < 2) continue ;
			candidates.put(group.getCommonAncestor().getXPath().toString(), group) ;
		}
		return candidates.values().toArray(new TNodeGroup[candidates.size()]) ;
	}
	
	
	
	static public class RepeatTNodeGroup {
		List<TNodeGroup> holder = new ArrayList<TNodeGroup>() ;

		public void add(TNodeGroup group) {
			holder.add(group) ;
		}
		
		public boolean sameStructure(TNodeGroup group) {
			if(holder.size() == 0) return false  ;
			return holder.get(0).sameStructure(group) ;
		}
		
		public float similarity(TNodeGroup group) {
			if(holder.size() == 0) return 0f  ;
			return holder.get(0).similarity(group) ;
		}
		
		public TNode getCommonAncestor() {
			if(holder.size() == 0) return null ;
			TNodeXPath ancestorXPath = null ;
			TNode firstAncestor = null;
			for(int i = 0; i < holder.size(); i++) {
				TNodeGroup group = holder.get(i) ;
				TNode node = group.getCommonAncestor() ;
				if(ancestorXPath == null) {
					firstAncestor = node ;
					ancestorXPath = node.getXPath() ;
				} else {
					ancestorXPath = node.getXPath().findFirstAncestor(ancestorXPath) ;
				}
			}
			return firstAncestor.getAncestor(ancestorXPath) ;
		}
	}
}