package net.datatp.xhtml.dom.processor;

import java.util.Iterator;
import java.util.List;

import net.datatp.xhtml.dom.TNode;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class CleanEmptyNodeProcessor extends TNodeProcessor {
  public void preTraverse(TNode node) {
    List<TNode> children = node.getChildren() ;
    if(children == null) return ;
    Iterator<TNode> i = children.iterator() ;
    TNode child = i.next() ;
    if(child.isEmpty()) {
      i.remove() ;
    } else if("select".equals(child.getNodeName()) || "input".equals(child.getNodeName()) || "textarea".equals(child.getNodeName())) {
      i.remove() ;
    }
  }
}