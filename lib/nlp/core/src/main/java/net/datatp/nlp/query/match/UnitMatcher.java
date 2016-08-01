package net.datatp.nlp.query.match;

import net.datatp.nlp.token.IToken;
import net.datatp.util.text.matcher.StringExpMatcher;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class UnitMatcher {
  private String name;
  private int    allowNextMatchDistance;

  public String getName() { return this.name ; }
  public void   setName(String name) { this.name = name ; }

  public int getAllowNextMatchDistance() { return this.allowNextMatchDistance ; }
  public void setAllowNextMatchDistance(int distance) { allowNextMatchDistance = distance ; }

  abstract public UnitMatch matches(IToken[] token, int pos) ;

  final protected boolean matches(StringExpMatcher[] matcher, String value) {
    if(matcher == null) return true ;
    for(StringExpMatcher sel : matcher) {
      if(sel.matches(value)) return true ;
    }
    return false  ;
  }

  final protected boolean matches(StringExpMatcher[] matcher, String[] value) {
    if(matcher == null) return true ;
    for(StringExpMatcher sel : matcher) {
      for(String selValue : value) {
        if(sel.matches(selValue)) return true ;
      }
    }
    return false  ;
  }
}
