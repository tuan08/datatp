package net.datatp.nlp.query.eval;

import java.io.IOException;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;


public class Number implements EvalExpression {
  private double value ;

  public String getName() { return "frequency"; }

  public Number(String exp) throws Exception {
    value = Double.parseDouble(exp) ;
  }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    return value ;
  }

  final static public boolean isNumber(String string) {
    int dotCount = 0 ;
    for(int i = 0; i < string.length(); i++) {
      char c = string.charAt(i) ;
      if(c == '.') dotCount++ ;
      else if(Character.isDigit(c)) continue ;
      else return false ;
    }
    if(dotCount > 1) return false; 
    return true  ;
  }
}