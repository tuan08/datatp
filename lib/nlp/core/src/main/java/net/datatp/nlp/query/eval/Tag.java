package net.datatp.nlp.query.eval;

import java.io.IOException;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;
import net.datatp.util.text.StringUtil;


public class Tag implements EvalExpression {
  private String[] tag;

  public Tag(String paramexp) throws Exception {
    tag = StringUtil.toStringArray(paramexp) ;
  }

  public String getName() { return "tag"; }


  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    for(String sel : tag) context.setTag(sel) ;
    return true ;
  }
}
