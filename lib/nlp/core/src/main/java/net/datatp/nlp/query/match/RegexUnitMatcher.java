package net.datatp.nlp.query.match;

import net.datatp.nlp.token.IToken;
import net.datatp.util.text.RegexMatcher;

public class RegexUnitMatcher extends UnitMatcher {
  private RegexMatcher regex ;

  public RegexUnitMatcher(String regex, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    this.regex = RegexMatcher.createAutomaton(regex.trim()) ;
  }

  public UnitMatch matches(IToken[] token, int pos) {
    String string = token[pos].getNormalizeForm() ;
    if(regex.matches(string)) {
      return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
    }
    return null ;
  }
}