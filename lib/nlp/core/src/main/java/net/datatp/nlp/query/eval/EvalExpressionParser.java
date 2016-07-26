package net.datatp.nlp.query.eval;

import net.datatp.nlp.query.match.MatcherResourceFactory;

public class EvalExpressionParser {
  final static public EvalExpression create(MatcherResourceFactory umFactory, String name, String paramExp) throws Exception {
    name = name.trim() ;
    if(paramExp != null) paramExp = paramExp.trim() ;
    if("if".equals(name)) {
      return new If(umFactory, paramExp) ;
    } else if("if-match".equals(name)) {
      return new IfMatch(umFactory, paramExp) ;
    } else if("exit".equals(name)) {
      return new Exit(paramExp) ;
    } else if("continue".equals(name)) {
      return new Continue() ;
    } else if("tag".equals(name)) {
      return new Tag(paramExp) ;
    } else if("extractCount".equals(name)) {
      return new ExtractCount(paramExp) ;
    } else if("print".equals(name)) {
      return new Print(paramExp) ;
    } else if(Number.isNumber(paramExp)) {
      return new Number(paramExp) ;
    }
    throw new Exception("Unknown expression " + name + "{" + paramExp + "}" ) ;
  }

  static public EvalExpression parse(MatcherResourceFactory umFactory, String exp) throws Exception {
    exp = exp.trim() ;
    if(exp.startsWith("$")) {
      exp = exp.substring(1);
      int openBraceIdx = exp.indexOf('{') ;
      String name = null, paramExp = null ;
      if(openBraceIdx < 0) {
        name = exp ;
      } else {
        name = exp.substring(0, openBraceIdx) ;
        paramExp = exp.substring(openBraceIdx + 1, exp.length() - 1) ;
      }
      return EvalExpressionParser.create(umFactory, name, paramExp) ;
    } 

    int colonIdx = exp.indexOf(':') ;
    if(colonIdx > 0) {
      String name = exp.substring(0, colonIdx).trim() ;
      String paramExp = exp.substring(colonIdx + 1).trim() ;
      return EvalExpressionParser.create(umFactory, name, paramExp) ;
    }

    if(Number.isNumber(exp)) return new Number(exp) ;
    throw new RuntimeException("The evaluation expression is incorrect: " + exp) ; 
  }
}