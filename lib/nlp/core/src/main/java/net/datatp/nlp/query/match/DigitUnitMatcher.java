package net.datatp.nlp.query.match;

import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.DigitTag;
import net.datatp.nlp.token.tag.TokenTag;


public class DigitUnitMatcher extends UnitMatcher {
  public DigitUnitMatcher(ParamHolder holder, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
  }

  public UnitMatch matches(IToken[] token, int pos) {
    List<TokenTag> tags = token[pos].getTag() ;
    if(tags == null) return null ;
    for(int i = 0; i < tags.size(); i++) {
      TokenTag tag = tags.get(i) ;
      if(!(tag instanceof DigitTag)) continue ;
      return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
    }
    return null ;
  }
}