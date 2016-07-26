package net.datatp.nlp.query.eval;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import net.datatp.nlp.query.QueryContext;
import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.query.match.RuleMatch;
import net.datatp.util.text.StringExpMatcher;
import net.datatp.util.text.StringUtil;

public class ExtractCount implements EvalExpression {
  private StringExpMatcher[] tagmatcher;

  public ExtractCount(String paramexp) throws Exception {
    String[] array = StringUtil.toStringArray(paramexp) ;
    tagmatcher = new StringExpMatcher[array.length] ;
    for(int i = 0; i < tagmatcher.length; i++) {
      tagmatcher[i] = new StringExpMatcher(array[i]) ;
    }
  }

  public String getName() { return "extractCount"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    int count = 0 ;
    for(RuleMatch sel : context.getRuleMatch()) {
      for(int i = 0; i < tagmatcher.length; i++) {
        count += count(tagmatcher[i], sel) ;
      }
    }
    return (double)count ;
  }

  private int count(StringExpMatcher matcher, RuleMatch rmatch) {
    int count  = 0 ;
    Map<String, String[]> extracts = rmatch.getExtracts() ;
    Iterator<String> i = extracts.keySet().iterator() ;
    while(i.hasNext()) {
      if(matcher.matches(i.next())) count++ ;
    }
    return count ;
  }
}