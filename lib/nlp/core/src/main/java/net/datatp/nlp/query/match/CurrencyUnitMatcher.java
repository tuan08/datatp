package net.datatp.nlp.query.match;

import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.CurrencyTag;
import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.util.text.matcher.StringExpMatcher;


public class CurrencyUnitMatcher extends UnitMatcher {
  private StringExpMatcher[] unitMatcher ;

  public CurrencyUnitMatcher(ParamHolder holder, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    String[] unit = holder.getFieldValue("unit") ;
    if(unit != null) unitMatcher = StringExpMatcher.create(unit) ;
  }

  public UnitMatch matches(IToken[] token, int pos) {
    List<TokenTag> tags = token[pos].getTag() ;
    if(tags == null) return null ;
    for(int i = 0; i < tags.size(); i++) {
      TokenTag tag = tags.get(i) ;
      if(!(tag instanceof CurrencyTag)) continue ;
      CurrencyTag implTag = (CurrencyTag) tag ;
      if(unitMatcher != null) {
        if(!matches(unitMatcher, implTag.getUnit())) return null ;
      }
      return new UnitMatch(implTag.getTagValue(), pos, pos + 1) ;
    }
    return null ;
  }
}