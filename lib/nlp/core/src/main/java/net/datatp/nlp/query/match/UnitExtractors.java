package net.datatp.nlp.query.match;

import net.datatp.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnitExtractors {
  private UnitExtractor[] unitExtractors ;

  public UnitExtractors(MatcherResourceFactory umFactory, String[] ruleExp) throws Exception {
    unitExtractors = new UnitExtractor[ruleExp.length] ;
    for(int i = 0; i < ruleExp.length; i++) {
      unitExtractors[i] = new UnitExtractor(umFactory, ruleExp[i]) ;
    }
  }

  public void process(RuleMatch[] ruleMatch) {
    for(int i = 0; i < ruleMatch.length; i++) {
      IToken[] tokens = ruleMatch[i].getTokenCollection().getTokens() ;
      for(UnitExtractor selExtractor : unitExtractors) {
        selExtractor.process(ruleMatch[i], tokens) ;
      }
    }
  }
}