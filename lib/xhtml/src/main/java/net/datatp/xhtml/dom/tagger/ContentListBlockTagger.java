package net.datatp.xhtml.dom.tagger;

import java.util.ArrayList;
import java.util.List;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroup;
import net.datatp.xhtml.dom.selector.Selector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ContentListBlockTagger extends Tagger {
  final static public String BLOCK_LIST = "block:list" ;
  final static public ContentListBlockTagger INSTANCE = new ContentListBlockTagger();
  
  public TNode[] tag(TDocument tdoc, TNode root) {
    Selector selector = new TNodeSelector() ;
    TNodeGroup[] groups = select(root, selector, 3) ;
    //dump(groups) ;
    //keep the valid group
    List<TNode> candidates = new ArrayList<TNode>() ;
    for(TNodeGroup group : groups) {
      if(isValid(group)) {
        candidates.add(group.getCommonAncestor()) ;
      }
    }
    if(candidates.size() < 5) return EMPTY ;
    groups = TNodeGroup.groupByCommonAncestor(candidates.toArray(new TNode[candidates.size()]), 3) ;
    TNode[] tagNodes = doTag(root, groups) ;
    //dump(groups) ;
    return tagNodes ;
  }


  boolean isValid(TNodeGroup group) {
    List<TNode> nodes = group.getTNodes() ;
    int textCount = 0, linkCount = 0 ;
    for(int i = 0; i < nodes.size(); i++) {
      TNode sel = nodes.get(i) ;
      if(sel.getAncestorByNodeName("a") != null) linkCount++ ;
      else textCount++ ;
    }
    if(textCount == 1 && linkCount >= 1)  return true ;
    return false ;
  }

  private TNode[] doTag(TNode root, TNodeGroup[] groups) {
    List<TNode> holder = new ArrayList<TNode>() ;
    for(TNodeGroup group : groups) {
      List<TNode> nodes = group.getTNodes() ;
      if(nodes.size() >= 5) {
        TNode ancestor = group.getCommonAncestor() ;
        ancestor.addTag(BLOCK_LIST) ;
        holder.add(ancestor) ;
      }
    }
    return holder.toArray(new TNode[holder.size()]) ;
  }

  static public class TNodeSelector implements Selector {
    public boolean isSelected(TNode node) {
      String ntext = node.getNodeValue() ;
      if(StringUtil.isEmpty(ntext)) return false ;
      if(ntext.length() > 75 && ntext.length() < 300) {
        return true ;
      } else if(ntext.length() > 20 && node.getAncestorByNodeName("a") != null) {
        return true ;
      }
      return false ;
    }
  }
}