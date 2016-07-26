package net.datatp.nlp.query.eval;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatch;
import net.datatp.nlp.query.match.RuleMatcher;

public class IfMatch implements EvalExpression {
  private EvalExpression match ;
  private EvalExpression notMatch ;

  private RuleMatcher  rule ; 

  public IfMatch() {} 

  public IfMatch(MatcherResourceFactory umFactory, String exp) throws Exception { 
    int idx = exp.indexOf("?") ;
    rule = new RuleMatcher(umFactory, exp.substring(0, idx)) ;
    exp = exp.substring(idx + 1) ;
    idx = exp.indexOf(":") ;

    if(idx > -1) {
      String matchExp = exp.substring(0, idx) ;
      match = EvalExpressionParser.parse(umFactory, matchExp) ;
      String notMatchExp = exp.substring(idx + 1) ;
      notMatch = EvalExpressionParser.parse(umFactory, notMatchExp) ;
    } else {
      match = EvalExpressionParser.create(umFactory, exp, exp) ;
    }
  }

  public String getName() { return "if-match"; }

  public Object eval(QueryContext context, QueryDocument doc) throws Exception {
    RuleMatch[] mresult = rule.matches(doc, 1)  ;
    boolean ret = mresult != null && mresult.length > 0 ;
    if(ret) {
      if(match != null)  return match.eval(context, doc) ;
      else return false ;
    } else {
      if(notMatch != null) notMatch.eval(context, doc) ;
      else return false ;
    }
    return ret ;
  }
}
