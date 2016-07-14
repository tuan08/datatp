package net.datatp.xhtml.dom.tagger;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroupByXPath;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TextLengthSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextBlockTagger extends Tagger {
  final static public String BLOCK_TEXT = "block:text" ;

  public TNode[] tag(TDocument tdoc, TNode node) {
    Selector selector = new TextLengthSelector(100, Integer.MAX_VALUE) ;
    TNodeGroupByXPath[] groups = doSelect(node, selector, 3) ;
    TNode[] tagNodes = tag(node, groups) ;
    return tagNodes ;
  }

  private TNode[] tag(TNode root, TNodeGroupByXPath[] groups) {
    List<TNode> holder = new ArrayList<TNode>() ;
    for(TNodeGroupByXPath group : groups) {
      List<TNode> nodes = group.getTNodes() ;
      if(nodes.size() < 2) continue ;
      int textSize = 0;
      for(int i = 0; i < nodes.size(); i++) {
        textSize += nodes.get(i).getNodeValue().length() ;
      }
      if(textSize < 300) continue ;
      TNode ancestor = nodes.get(0).getAncestor(group.xpathNode) ;
      ancestor.addTag(BLOCK_TEXT) ;
      holder.add(ancestor) ;
    }
    if(holder.size() == 0) return EMPTY ;
    return holder.toArray(new TNode[holder.size()]) ;
  }
}