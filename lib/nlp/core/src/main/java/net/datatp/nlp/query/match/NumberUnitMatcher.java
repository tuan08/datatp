package net.datatp.nlp.query.match;

import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.DigitTag;
import net.datatp.nlp.token.tag.NumberTag;
import net.datatp.nlp.token.tag.TokenTag;


public class NumberUnitMatcher extends UnitMatcher {
  public NumberUnitMatcher(ParamHolder holder, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
  }

  public UnitMatch matches(IToken[] token, int pos) {
    List<TokenTag> tags = token[pos].getTag() ;
    for(int i = 0; i < tags.size(); i++) {
      TokenTag tag = tags.get(i) ;
      if(tag instanceof NumberTag) {
        return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
      }
      if(tag instanceof DigitTag) {
        return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
      }
    }
    return null ;
  }
}