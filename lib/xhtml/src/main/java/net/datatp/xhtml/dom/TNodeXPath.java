package net.datatp.xhtml.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeXPath {
	private String[] xpath ;

	public TNodeXPath(String xpath) {
		this.xpath = xpath.split("/") ;
	}

	public TNodeXPath(String[] xpath) {
		this.xpath = xpath ;
	}
	
	public TNodeXPath(TNode node) {
		ArrayList<TNode> hierarchy = new ArrayList<TNode>(50);
		while (node != null) {
			hierarchy.add(node);
			node = node.getParent();
		}

		TNode parent = null ;
		xpath = new String[hierarchy.size()] ;
		for(int i = hierarchy.size() - 1; i >= 0; i--) {
			node = hierarchy.get(i) ;
			int index = 0 ;
			if(parent != null) {
				List<TNode> children = parent.getChildren() ;
				for(int k = 0 ; k < children.size(); k++) {
					TNode child = children.get(k) ;
					if(child == node) {
						index = k ;
						break ;
					}
				}
			}
			xpath[hierarchy.size() - i - 1] = node.getNodeName() + "[" + index + "]";
			parent = node ;
		}
	}

	public String[] getXPath() { return this.xpath ; }
	
	public int getDeep() { return xpath.length ; }
	
	public TNodeXPath findFirstAncestor(TNodeXPath other) {
		int limit = xpath.length ;
		if(other.xpath.length < limit) limit = other.xpath.length ;
		List<String> holder = new ArrayList<String>() ;
		for(int i = 0; i < limit; i++) {
			if(xpath[i].equals(other.xpath[i])) {
				holder.add(xpath[i]) ;
			} else {
				break ;
			}
		}
		return new TNodeXPath(holder.toArray(new String[holder.size()])) ;
	}
	
	public String[] getSubXPath(int from, int to) { 
		if(to < xpath.length) return null ;
		int size = to - from ;
		String[] subxpath = new String[size] ;
		for(int i = from; i < to; i++) subxpath[i - from] = xpath[i] ;
		return subxpath ; 
	}
	
	public boolean isSubXPath(int from, String[] subxpath) {
		if(from + subxpath.length > xpath.length) return false ;
		for(int i = 0; i < subxpath.length; i++) {
			if(!subxpath[i].equals(xpath[from + i])) return false;
		}
		return true ;
	}
	
	public int differentDistance(TNodeXPath other) {
		int maxDistance = xpath.length; 
		int minDistance = xpath.length ;
		if(maxDistance < other.xpath.length) maxDistance = other.xpath.length ;
		if(minDistance > other.xpath.length) minDistance = other.xpath.length ;
		for(int i = 0; i < minDistance; i++) {
			if(!xpath[i].equals(other.xpath[i])) {
				return maxDistance - i - 1 ; 
			}
		}
		return maxDistance ;
	}
	
	public int similarDistance(TNodeXPath other) {
		int maxDistance = xpath.length; 
		int minDistance = xpath.length ;
		if(maxDistance < other.xpath.length) maxDistance = other.xpath.length ;
		if(minDistance > other.xpath.length) minDistance = other.xpath.length ;
		for(int i = 0; i < minDistance; i++) {
			if(!xpath[i].equals(other.xpath[i])) {
				return i; 
			}
		}
		return minDistance ;
	}
	
	public boolean equals(TNodeXPath other) {
		if(xpath.length != other.xpath.length) return false ;
		for(int i = 0; i < xpath.length; i++) {
			if(!xpath[i].equals(other.xpath[i])) return false ;
		}
		return true ;
	}
	
	public boolean equalsIgnoreIndex(TNodeXPath other) {
		if(xpath.length != other.xpath.length) return false ;
		for(int i = 0; i < xpath.length; i++) {
			if(!compareXPathName(xpath[i], other.xpath[i])) return false ;
		}
		return true ;
	}
	
	private boolean compareXPathName(String p1, String p2) {
		int limit = p1.length() ;
		if(p2.length() < limit) limit = p2.length() ;
		for(int i = 0; i < limit; i++) {
			char c1 = p1.charAt(i) ;
			char c2 = p2.charAt(i) ;
			if(c1 != c2) return false ; 
			if(c1 == '[')  return true ;
		}
		return true ;
	}
	
	public String getXPathName() {
		StringBuilder b = new StringBuilder() ;
		for(int i = 0; i < xpath.length; i++) {
			if(i > 0) b.append("/") ;
			String name = xpath[i].substring(0, xpath[i].indexOf('[')) ;
			b.append(name) ;
		}
		return b.toString() ;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder() ;
		for(int i = 0; i < xpath.length; i++) {
			if(i > 0) b.append("/") ;
			b.append(xpath[i]) ;
		}
		return b.toString() ;
	}
}