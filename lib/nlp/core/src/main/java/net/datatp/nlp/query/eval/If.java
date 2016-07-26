package net.datatp.nlp.query.eval;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.query.match.MatcherResourceFactory;

public class If implements EvalExpression {
  private EvalExpression leftExp ;
  private EvalExpression rightExp ;
  private OpComparator   opComparator ;

  private EvalExpression matchExp ;
  private EvalExpression notMatchExp ;

  public String getName() { return "if"; }

  public If(MatcherResourceFactory umFactory, String exp) throws Exception { 
    int idx = exp.indexOf('?') ;
    parseCondition(umFactory, exp.substring(0, idx)) ;
    parseOperation(umFactory, exp.substring(idx + 1)) ;
  }

  public Object eval(QueryContext context, QueryDocument doc) throws Exception {
    boolean compareResult = false ;
    if(opComparator instanceof NoOpComparator) compareResult = (Boolean) leftExp.eval(context, doc) ;
    else compareResult = opComparator.compare(leftExp, rightExp, context, doc) ;

    if(compareResult) {
      matchExp.eval(context, doc) ;
    } else {
      if(notMatchExp != null) notMatchExp.eval(context, doc) ;
    }
    return compareResult;
  }

  private void parseCondition(MatcherResourceFactory umFactory, String exp) throws Exception {
    String[] pairexp ;
    if(exp.indexOf(" == ") > 0) {
      opComparator = new EqualOpComparator() ;
      pairexp = exp.split(" == ", 2) ;
    } else if(exp.indexOf(" != ") > 0) {
      opComparator = new NotEqualOpComparator() ;
      pairexp = exp.split(" != ", 2) ;
    } else if(exp.indexOf(" > ") > 0) {
      opComparator = new GreaterOpComparator() ;
      pairexp = exp.split(" > ", 2) ;
    } else if(exp.indexOf(" >= ") > 0) {
      opComparator = new GreaterOrEqualOpComparator() ;
      pairexp = exp.split(" >= ", 2) ;
    } else if(exp.indexOf(" < ") > 0) {
      opComparator = new LessOpComparator() ;
      pairexp = exp.split(" < ", 2) ;
    } else if(exp.indexOf(" <= ") > 0) {
      opComparator = new LessOrEqualOpComparator() ;
      pairexp = exp.split(" <= ", 2) ;
    } else {
      opComparator = new NoOpComparator() ;
      pairexp = new String[] { exp } ;
    }
    leftExp = EvalExpressionParser.parse(umFactory, pairexp[0]) ;
    if(pairexp.length > 1) rightExp = EvalExpressionParser.parse(umFactory, pairexp[1]) ;
  }

  private void parseOperation(MatcherResourceFactory umFactory, String exp) throws Exception {
    String[] pairexp = exp.split(" : ", 2) ;
    this.matchExp = EvalExpressionParser.parse(umFactory, pairexp[0]) ;
    if(pairexp.length == 2) this.notMatchExp = EvalExpressionParser.parse(umFactory, pairexp[1]) ;
    if(matchExp == null) throw new RuntimeException("Evaluation expression is incorrect: " + exp) ;
  }

  static interface OpComparator {
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception ;
  }

  static class NoOpComparator implements OpComparator{
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception {
      return (Boolean) leftOp.eval(context, doc) ;
    }
  }

  static class EqualOpComparator implements OpComparator{
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ;
      return (Boolean) (left.compareTo(right) == 0) ;
    }
  }

  static class NotEqualOpComparator implements OpComparator{
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ;
      return (Boolean) (left.compareTo(right) != 0) ;
    }
  }

  static class LessOpComparator implements OpComparator{
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ;
      return (Boolean) (left.compareTo(right) < 0) ;
    }
  }

  static class GreaterOpComparator implements OpComparator{
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp, 
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ;
      return (Boolean) (left.compareTo(right) > 0) ;
    }
  }

  static class GreaterOrEqualOpComparator implements OpComparator {
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp,
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ; 
      return (Boolean) (left.compareTo(right) >= 0) ;
    }
  }

  static class LessOrEqualOpComparator implements OpComparator {
    public boolean compare(EvalExpression leftOp, EvalExpression rightOp,
        QueryContext context, QueryDocument doc) throws Exception {
      Comparable left = (Comparable)leftOp.eval(context, doc) ;
      Comparable right = (Comparable)rightOp.eval(context, doc) ; 
      return (Boolean) (left.compareTo(right) <= 0) ;
    }
  }
}