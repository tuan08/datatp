package net.datatp.nlp.query;

import net.datatp.nlp.query.eval.EvalExpression;
import net.datatp.nlp.query.eval.EvalExpressionParser;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatch;
import net.datatp.nlp.query.match.RuleMatchers;
import net.datatp.nlp.query.match.UnitExtractors;
import net.datatp.nlp.token.TokenCollection;

public class Query {
  private String   name ;
  private int      priority ;
  private String   description ;

  private int      matchmax = 1;
  private String   matchselector = "first";
  private String[] prematch ;
  private String[] match ;
  private String[] extract ;
  private String[] postmatch ;

  transient private EvalExpression[] preMatchEval ;
  transient private RuleMatchers ruleMatchers ;
  transient private UnitExtractors unitExtractors ;
  transient private EvalExpression[] postMatchEval ;

  public Query() { }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getPriority() { return priority; }
  public void setPriority(int priority) { this.priority = priority; }

  public String getDescription() { return description; }
  public void   setDescription(String description) { this.description = description; }

  public int  getMatchmax() { return this.matchmax ; }
  public void setMatchmax(int max) { this.matchmax = max ; } 

  public String getMatchselector() { return matchselector ; }
  public void   setMatchselector(String name) { this.matchselector = name; }

  public String[] getPrematch() { return prematch; }
  public void setPrematch(String[] prematch) { this.prematch = prematch; }

  public String[] getMatch() { return match; }
  public void setMatch(String[] match) { this.match = match; }

  public String[] getExtract() { return extract; }
  public void setExtract(String[] extract) { this.extract = extract; }

  public String[] getPostmatch() { return postmatch; }
  public void setPostmatch(String[] postmatch) { this.postmatch = postmatch; }

  public void compile(MatcherResourceFactory umFactory) throws Exception {
    if(prematch != null) {
      preMatchEval = new EvalExpression[prematch.length] ;
      for(int i = 0; i < prematch.length; i++) {
        preMatchEval[i] = EvalExpressionParser.parse(umFactory, prematch[i]) ;
      }
    }
    ruleMatchers = new RuleMatchers(umFactory, match, matchmax) ;
    unitExtractors = new UnitExtractors(umFactory, extract) ;

    if(postmatch != null) {
      postMatchEval = new EvalExpression[postmatch.length] ;
      for(int i = 0; i < postmatch.length; i++) {
        postMatchEval[i] = EvalExpressionParser.parse(umFactory, postmatch[i]) ;
      }
    }
  }

  public void query(QueryContext context, TokenCollection doc) throws Exception {
    RuleMatch[] rmatch = ruleMatchers.matches(doc) ;
    context.setRuleMatch(rmatch) ;
  }

  public void query(QueryContext context, QueryDocument doc) throws Exception {
    if(preMatchEval != null) {
      for(int i = 0; i < preMatchEval.length; i++) {
        preMatchEval[i].eval(context, doc) ;
        if(context.isComplete()) return ;
        if(context.isContinue()) break ;
      }
    }
    RuleMatch[] rmatch = ruleMatchers.matches(doc) ;
    if(rmatch == null) return;
    unitExtractors.process(rmatch) ;
    context.setRuleMatch(rmatch) ;
    if(postMatchEval != null) {
      for(int i = 0; i < postMatchEval.length; i++) {
        postMatchEval[i].eval(context, doc) ;
        if(context.isComplete()) return ;
        if(context.isContinue()) break ;
      }
    }
  }
}