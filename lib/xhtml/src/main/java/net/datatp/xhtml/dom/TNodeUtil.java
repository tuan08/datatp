package net.datatp.xhtml.dom;

import java.util.ArrayList;
import java.util.List;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 7, 2010  
 */
public class TNodeUtil {
  static public String getTitle(TNode doc)  {
    TNode title = findFirstNodeByTagName(doc, "title") ;
    if(title != null) return title.getTextContent();
    return null ;
  }
  
  static public String getBase(TNode node)  {
    TNode base = findFirstNodeByTagName(node, "base") ;
    if(base == null) return null ;
    return base.getAttribute("href") ;
  }
  
  static public TNode findFirstNodeByTagName(TNode node, String tag) {
    if(node == null) return null ;
    if(tag.equalsIgnoreCase(node.getNodeName())) return node ;
    List<TNode> children = node.getChildren() ;
    if(children == null) return null ;
    for(int i = 0; i < children.size(); i++) {
      TNode child = children.get(i) ;
      TNode ret = findFirstNodeByTagName(child, tag) ;
      if(ret != null) return ret ;
    }
    return null ;
  }
  
  static public void findNodeByTagName(List<TNode> holder, TNode node, String tag, int deep, int maxDeep) {
  	if(deep == maxDeep) return ; 
  	if(node == null) return  ;
  	if(tag.equalsIgnoreCase(node.getNodeName())) {
  		holder.add(node) ;
  	}
  	List<TNode> children = node.getChildren() ;
  	if(children == null) return ;
  	for(int i = 0; i < children.size() ; i++) {
  		TNode child = children.get(i) ;
  		findNodeByTagName(holder, child, tag, deep + 1, maxDeep) ;
  	}
  }
  
  static public TNode getAncestor(TNode e, String tag, int limit) {
    int backLevel = 0 ;
    while(e != null && backLevel < limit) {
      if(tag.equals(e.getNodeName())) return e ;
      e = e.getParent() ;
      backLevel++ ;
    }
    return null ;
  }
  
  static public TNode[] findMetaNode(TNode root) {
  	List<TNode> holder = new ArrayList<TNode>() ;
  	findNodeByTagName(holder, root, "meta", 0, 4) ;
  	return holder.toArray(new TNode[holder.size()]) ;
  }
  
  static public String findRefreshMetaNodeUrl(TNode root) {
  	for(TNode sel : findMetaNode(root)) {
  		String httpEquiv = sel.getAttribute("http-equiv") ;
  		if("refresh".equalsIgnoreCase(httpEquiv)) {
  			String content = sel.getAttribute("content") ;
  			if(content == null) continue ;
  			String[] array = content.split(";") ;
  			for(String selStr : array) {
  				String normStr = selStr.trim().toLowerCase() ;
  				if(normStr.startsWith("url")) {
  					int idx = selStr.indexOf("=") ;
  					return selStr.substring(idx + 1) ;
  				}
  			}
  		}
  	}
  	return null ;
  }
}