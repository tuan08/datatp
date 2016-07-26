package net.datatp.nlp.query.eval;

import java.io.IOException;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.query.match.MatcherResourceFactory;


public class Continue implements EvalExpression {

  public Continue() {} 

  public String getName() { return "continue"; }

  public void init(MatcherResourceFactory umFactory, String paramexp) throws Exception {
  }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    context.setContinue(true) ;
    return true ;
  }
}
