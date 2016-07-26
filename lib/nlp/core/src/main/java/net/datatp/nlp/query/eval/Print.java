package net.datatp.nlp.query.eval;

import java.io.IOException;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;


public class Print implements EvalExpression {
  private String msg ;

  public Print(String paramexp) { msg = paramexp ; }

  public String getName() { return "print"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    System.out.println(msg);
    return true ;
  }
}