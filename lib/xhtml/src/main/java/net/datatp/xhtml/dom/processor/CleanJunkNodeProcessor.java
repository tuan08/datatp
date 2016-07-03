package net.datatp.xhtml.dom.processor;

import java.util.Iterator;
import java.util.List;

import net.datatp.xhtml.dom.TNode;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class CleanJunkNodeProcessor extends TNodeProcessor {
  final static public String[] PATTERN = {
      "menu", "navigation", "nav-", "nav_", "breadcumbs", "banner", "footer" 
  };

  protected void preTraverse(TNode node) {
    List<TNode> children = node.getChildren() ;
    if(children == null) return ;
    Iterator<TNode> i = children.iterator() ;
    while(i.hasNext()) {
      TNode child = i.next() ;
      if(isJunkNode(child)) {
        i.remove() ;
      } else if("select".equals(child.getNodeName()) || "input".equals(child.getNodeName()) || "textarea".equals(child.getNodeName())) {
        i.remove() ;
      }
    }
  }

  private boolean isJunkNode(TNode node) {
    if(matchPattern(node.getCssClass())) return true ;
    if(matchPattern(node.getElementId())) return true ;
    return false  ;
  }

  private boolean matchPattern(String string) {
    if(string == null) return false ;
    for(String sel : PATTERN) {
      if(string.indexOf(sel) >= 0) {
        return true ;
      }
    }
    return false  ;
  }
}