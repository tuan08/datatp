package net.datatp.xhtml.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeGroupByXPath {
  public String      xpath ;
  public TNodeXPath  xpathNode ;
  public List<TNode> holder = new ArrayList<TNode>() ;

  TNodeGroupByXPath(TNodeXPath xpath) {
    this.xpathNode = xpath ;
    this.xpath     = xpath.toString() ;
  }

  public void add(TNode node) { holder.add(node) ; }

  public List<TNode> getTNodes() { return this.holder ; }

  public TNode getCommonAncestor() {
    TNode node = holder.get(0) ;
    return node.getAncestor(xpathNode) ;
  }

  static public TNodeGroupByXPath[] group(TNode[] nodes, int maxDiffDistance) {
    Map<String, TNodeGroupByXPath> map = new LinkedHashMap<String, TNodeGroupByXPath>() ;
    TNode previousNode = null ;
    boolean previousNodeAdded = false ;
    for(int i = 0; i < nodes.length; i++) {
      TNode sel = nodes[i] ;
      if(previousNode != null && previousNode.getXPath().differentDistance(sel.getXPath()) <  maxDiffDistance) {
        TNodeXPath ancestor = sel.getXPath().findFirstAncestor(previousNode.getXPath()) ;
        TNodeGroupByXPath group = map.get(ancestor.toString()) ;
        if(group != null) {
          group.add(sel) ;
          previousNodeAdded = true ;
        } else if(!previousNodeAdded) {
          group = new TNodeGroupByXPath(ancestor) ;
          group.add(previousNode) ;
          group.add(sel) ;
          map.put(group.xpath, group) ;
          previousNodeAdded = true ;
        } else {
          previousNodeAdded = false ;
        }
      } else {
        previousNodeAdded = false ;
      }
      previousNode = nodes[i] ;
    }
    return map.values().toArray(new TNodeGroupByXPath[map.size()]) ;
  }
}