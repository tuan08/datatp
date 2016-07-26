package net.datatp.nlp.query.match;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;


/**
 * $Author: Tuan Nguyen$ 
 **/
public class TreeWordMatchers {
  private TreeWordMatcher[] unitMatchers ;

  public void add(String name, String[] word) throws Exception {
    TreeWordMatcher unitMatcher = new TreeWordMatcher(word) ;
    unitMatcher.setName(name) ;
    List<TreeWordMatcher> holder = new ArrayList<TreeWordMatcher>() ;
    if(unitMatchers != null) {
      for(TreeWordMatcher sel : unitMatchers) holder.add(sel) ;
    }
    holder.add(unitMatcher) ;
    unitMatchers = holder.toArray(new TreeWordMatcher[holder.size()]) ;
  }

  public UnitMatch[] matches(IToken[] token) {
    return matches(token, 0, token.length) ;
  }

  public UnitMatch[] matches(IToken[] token, int from, int to) {
    List<UnitMatch> holder  = null ;
    int pos = from ;
    while(pos < to) {
      UnitMatch unitMatch = null ;
      for(TreeWordMatcher sel : unitMatchers) {
        unitMatch = sel.matches(token, pos) ;
        if(unitMatch != null) break ; 
      }
      if(unitMatch != null) {
        if(holder == null) holder = new ArrayList<UnitMatch>() ;
        holder.add(unitMatch) ;
        pos = unitMatch.getTo() ;
      } else {
        pos++ ;
      }
    }
    if(holder == null) return null ;
    return holder.toArray(new UnitMatch[holder.size()]) ;
  }
}
