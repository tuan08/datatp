package net.datatp.xhtml.dom.tagger;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroupByXPath;
import net.datatp.xhtml.dom.selector.TextSimilaritySelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class KeywordBlockTagger extends Tagger {
  private String tag ;
  private TextSimilaritySelector selector ;

  public KeywordBlockTagger(String tag, String[] keyword) {
    this.tag = tag ;
    this.selector = new TextSimilaritySelector(keyword) ;
  }

  public String getTag() { return this.tag ; }

  public TNode[] tag(TDocument tdoc, TNode node) {
    TNodeGroupByXPath[] groups = doSelect(node, selector, 5) ;
    TNode[] tagNodes = tag(node, groups) ;
    //dump(groups) ;
    return tagNodes ;
  }

  private TNode[] tag(TNode root, TNodeGroupByXPath[]  groups) {
    List<TNode> holder = new ArrayList<TNode>() ;
    for(TNodeGroupByXPath group : groups) {
      List<TNode> nodes = group.getTNodes() ;
      if(nodes.size() < 2) continue ;
      TNode ancestor = nodes.get(0).getAncestor(group.xpathNode) ;
      ancestor.addTag(tag) ;
      holder.add(ancestor) ;
    }
    return holder.toArray(new TNode[holder.size()]) ;
  }
}