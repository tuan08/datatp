package net.datatp.nlp.query.eval;

import java.io.IOException;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;


public class Exit implements EvalExpression {
  public Exit(String exp) throws Exception {
  }

  public String getName() { return "exit"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    context.setComplete(true) ;
    return true ;
  }
}