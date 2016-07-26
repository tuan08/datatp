package net.datatp.nlp.query.match;

import net.datatp.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnitExtractor {
  final static byte SET = 0, ADD = 1 ;

  private String ruleExp ;
  private String name    ;
  private byte   operator = SET ;
  private UnitMatcher unitMatcher ;

  public UnitExtractor(MatcherResourceFactory umFactory, String ruleExp) throws Exception {
    this.ruleExp = ruleExp ;
    if(ruleExp.indexOf("+=") > 0) {
      operator = ADD ;
      parse(umFactory, ruleExp, "+=") ;
    } else {
      operator = SET ;
      parse(umFactory, ruleExp, "=") ;
    }
  }

  public String getRuleExp() { return this.ruleExp ; }

  public void process(RuleMatch rmatch, IToken[] tokens) {
    if(operator == ADD) {
      int pos = 0 ;
      while(pos < tokens.length) {
        UnitMatch unitMatch = unitMatcher.matches(tokens, pos) ;
        if(unitMatch != null) {
          rmatch.addExtract(name, unitMatch.getWord()) ;
          pos = unitMatch.getTo() ;
        } else {
          pos++ ;
        }
      }
    } else {
      for(int i = 0; i < tokens.length; i++) {
        UnitMatch unitMatch = unitMatcher.matches(tokens, i) ;
        if(unitMatch != null) {
          rmatch.setExtract(name, unitMatch.getWord()) ;
          return ;
        }
      }
    }
  }

  private void parse(MatcherResourceFactory umFactory, String exp, String operator) throws Exception {
    int idx = exp.indexOf(operator) ;
    this.name = exp.substring(0, idx).trim() ;;
    String unitMatchExp = exp.substring(idx + operator.length()) ;
    unitMatcher = umFactory.create(unitMatchExp, null);
  }
}
