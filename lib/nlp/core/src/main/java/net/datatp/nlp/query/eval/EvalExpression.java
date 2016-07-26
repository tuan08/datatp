package net.datatp.nlp.query.eval;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;

public interface EvalExpression {
  public String getName() ;
  public Object eval(QueryContext context, QueryDocument doc) throws Exception ;
}
