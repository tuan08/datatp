package net.datatp.xhtml.dom;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.xhtml.dom.selector.Selector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeGroup {
	private TNode commonAncestor ;
	List<TNode> holder = new ArrayList<TNode>() ;
	
  public TNodeGroup() {
	}
	
  public TNodeGroup(TNode commonAncestor) {
  	this.commonAncestor = commonAncestor ;
  }
  
  public TNodeGroup(TNode commonAncestor, Selector childrenSelector) {
  	this.commonAncestor = commonAncestor ;
  	if(commonAncestor == null) return ;
  	TNode[] children = commonAncestor.select(childrenSelector, false);
  	for(TNode sel : children) holder.add(sel) ;
  }
  
	public boolean add(TNode node, int maxDiffDistance) { 
		if(holder.size() > 0) {
			TNode first = holder.get(0) ;
			int diffDistance = first.getXPath().differentDistance(node.getXPath()) ;
			if(diffDistance > maxDiffDistance) return false ;
		}
		holder.add(node) ;
		commonAncestor = null ;
		return true ;
	}
	
	public void add(TNode node) { 
		holder.add(node) ;
	}

	public void merge(TNodeGroup other) {
		for(int i = 0; i < other.holder.size(); i++) {
			holder.add(other.holder.get(i)) ;
		}
		commonAncestor = null ;
	}
	
	public List<TNode> getTNodes() { return this.holder ; }
	
	public boolean hasSimilarNode(TNode node) {
		String cssClass = node.getCssClass() ;
		String text = node.getNodeValue() ;
		for(int i = 0 ; i < holder.size(); i++) {
			TNode sel = holder.get(i) ;
			if(sel.getXPath().equalsIgnoreIndex(node.getXPath())) {
				if(cssClass != null && cssClass.equals(sel.getCssClass())) return true ;
				if(text != null  && text.equals(sel.getNodeValue())) return true; 
			}
		}
		return false ;
	}
	
	public TNode getCommonAncestor() {
		if(holder.size() == 0) return null ;
		if(commonAncestor != null) return commonAncestor ;
		TNodeXPath ancestor = null ;
		for(int i = 0; i < holder.size(); i++) {
			TNode node = holder.get(i) ;
			if(ancestor == null) {
				ancestor = node.getXPath() ;
			} else {
				ancestor = node.getXPath().findFirstAncestor(ancestor) ;
			}
		}
		commonAncestor = holder.get(0).getAncestor(ancestor) ;
		return commonAncestor ;
	}
	
	public boolean sameStructure(TNodeGroup other) {
		if(holder.size() != other.holder.size()) return false ;
		if(!getCommonAncestor().getXPath().equalsIgnoreIndex(other.getCommonAncestor().getXPath())) return false ;
		for(int i = 0; i < holder.size(); i++) {
			TNode node1 = holder.get(i) ;
			TNode node2 = other.holder.get(i) ;
			if(!node1.getXPath().equalsIgnoreIndex(node2.getXPath())) return false ;
		}
		return true ;
	}
	
	public float similarity(TNodeGroup other) {
		if(!getCommonAncestor().getXPath().equalsIgnoreIndex(other.getCommonAncestor().getXPath())) {
			return 0f ;
		}
		float sum = 0 ;
		for(int i = 0; i < holder.size(); i++) {
			TNode node1 = holder.get(i) ;
			String cssClass = node1.getCssClass() ;
			for(int j = 0; j < other.holder.size(); j++) {
				TNode node2 = other.holder.get(j) ;
				if(node1.getXPath().equalsIgnoreIndex(node2.getXPath()))  {
					if(cssClass != null) {
						if(cssClass.equals(node2.getCssClass())) {
							sum += 1f ;
							break ;
						}
					} else {
						sum += 1f ;
						break ;
					}
				}
			}
		}
		return sum/other.holder.size() ;
	}
	
	public void dump(PrintStream out) {
		TNode ancestor = getCommonAncestor() ;
		if(ancestor == null) return ;
		out.println("---------------------------------------------------------------------");
		out.println("XPATH: " + ancestor.getXPath() + "{" + holder.size() + "}");
//		for(int i = 0; i < holder.size(); i++) {
//			TNode node = holder.get(i) ;
//			out.println("       " + node.getXPath());
//		}
		//out.println("TEXT: "  + ancestor.getTextContent());
	}
	
	public void dumpPath(PrintStream out) {
		TNode ancestor = getCommonAncestor() ;
		if(ancestor == null) return ;
		out.println("XPATH: " + ancestor.getXPath() + "{" + holder.size() + "}");
		for(int i = 0; i < holder.size(); i++) {
			TNode node = holder.get(i) ;
			out.println("       " + node.getXPath());
		}
	}
	
	static public void dump(PrintStream out, TNodeGroup[] groups) {
		for(TNodeGroup sel : groups) sel.dump(out) ;
	}
	
	static public TNodeGroup[] groupBySimilarTNode(TNode[] nodes, int maxDiffDistance) {
		List<TNodeGroup> holder  = new ArrayList<TNodeGroup>() ;
		TNodeGroup currGroup = new TNodeGroup() ;
		for(int i = 0; i < nodes.length; i++) {
			TNode sel = nodes[i] ;
			if(currGroup.hasSimilarNode(sel)) {
				holder.add(currGroup) ;
				currGroup = new TNodeGroup() ;
				currGroup.add(sel, maxDiffDistance) ;
			} else if(!currGroup.add(sel, maxDiffDistance)) {
				holder.add(currGroup) ;
				currGroup = new TNodeGroup() ;
				currGroup.add(sel, maxDiffDistance) ;
			}
		}
		if(currGroup.getTNodes().size() > 0) {
			holder.add(currGroup) ;
		}
		LinkedHashMap<String, TNodeGroup> map = new LinkedHashMap<String, TNodeGroup>() ; 
		for(int i = 0; i < holder.size(); i++) {
			TNodeGroup group = holder.get(i) ;
			map.put(group.getCommonAncestor().getXPath().toString(), group) ;
		}
		return map.values().toArray(new TNodeGroup[map.size()]) ;
	}
	
	static public TNodeGroup[] groupByCommonAncestor(TNode[] nodes, int maxDiffDistance) {
		Map<String, TNodeGroup> map = new LinkedHashMap<String, TNodeGroup>() ;
		TNode previousNode = null ;
	  boolean previousNodeAdded = false ;
		for(int i = 0; i < nodes.length; i++) {
			TNode sel = nodes[i] ;
			if(previousNode != null &&
				 previousNode.getXPath().differentDistance(sel.getXPath()) <  maxDiffDistance) {
				TNodeXPath ancestorXPath = 
					sel.getXPath().findFirstAncestor(previousNode.getXPath()) ;
				String key = ancestorXPath.toString() ;
				TNodeGroup group = map.get(key) ;
				if(group != null) {
					group.add(sel) ;
					previousNodeAdded = true ;
				} else if(!previousNodeAdded) {
					group = new TNodeGroup(sel.getAncestor(ancestorXPath)) ;
					group.add(previousNode) ;
					group.add(sel) ;
					map.put(key, group) ;
					previousNodeAdded = true ;
				} else {
					previousNodeAdded = false ;
				}
			} else {
				previousNodeAdded = false ;
			}
			previousNode = nodes[i] ;
		}
		return map.values().toArray(new TNodeGroup[map.size()]) ;
	}
	
	static public TNodeGroup[] filterTNodeGroupByOccurence(TNodeGroup[] groups, int minOcc) {
		List<TNodeGroup> holder = new ArrayList<TNodeGroup>() ;
		for(int i = 0; i < groups.length; i++) {
			TNodeGroup sel = groups[i] ;
			if(sel.getTNodes().size() >= minOcc) {
				holder.add(sel);
			}
		}
		return holder.toArray(new TNodeGroup[holder.size()]);
	}
	
	static public TNodeGroup[] mergeTNodeGroupByCommonAncestor(TNodeGroup[] groups) {
		Map<String, TNodeGroup> holder = new LinkedHashMap<String, TNodeGroup>() ;
		for(int i = 0; i < groups.length; i++) {
			TNode  commonAncestor = groups[i].getCommonAncestor() ;
			String xpath = commonAncestor.getXPath().toString() ;
			TNodeGroup commonGroup = holder.get(xpath) ;
			if(commonGroup != null) commonGroup.merge(groups[i]) ;
			else holder.put(xpath, groups[i]) ;
		}
		return holder.values().toArray(new TNodeGroup[holder.size()]);
	}
	
	static public TNodeGroup[] findRepeatTNodeGroup(TNodeGroup[] groups) {
		List<TNodeGroup> holder = new ArrayList<TNodeGroup>() ;
		for(int i = 0; i < groups.length; i++) {
			TNodeGroup sel = groups[i] ;
			for(int j = 0; j < groups.length; j++) {
				if(sel == groups[j]) continue ;
				if(sel.sameStructure(groups[j])) {
					holder.add(sel);
					break ;
				}
			}
		}
		return holder.toArray(new TNodeGroup[holder.size()]);
	}
	
	static public TNode getCommonAncestor(TNodeGroup[] groups) {
		if(groups == null ||  groups.length == 0) return null ;
		TNodeXPath ancestor = null ;
		TNode first = null ;
		for(int i = 0; i < groups.length; i++) {
			TNode node = groups[i].getCommonAncestor() ;
			if(ancestor == null) {
				ancestor = node.getXPath() ;
				first = node ;
			} else {
				ancestor = node.getXPath().findFirstAncestor(ancestor) ;
			}
		}
		return first.getAncestor(ancestor) ;
	}
}