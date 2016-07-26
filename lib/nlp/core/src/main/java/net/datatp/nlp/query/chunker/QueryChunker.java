package net.datatp.nlp.query.chunker;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatch;
import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class QueryChunker implements TokenAnalyzer {
  private MatcherResourceFactory mrFactory ;
  private RuleMatcher[] matcher;

  public QueryChunker() {
    mrFactory = new MatcherResourceFactory(new SynsetDictionary(), new EntityDictionary()) ;
  }

  public QueryChunker(MatcherResourceFactory mrFactory) {
    this.mrFactory = mrFactory ;
  }

  public void defineSynset(String name, String[] type, String[] variant) {
    mrFactory.getSynsetDictionary().add(name, type, variant) ;
  }

  public void defineEntity(String name, String[] type, String[] variant) {
    mrFactory.getEntityDictionary().add(name, type, variant) ;
  }

  public void defineMatches(String ... exp) throws Exception {
    matcher = new RuleMatcher[exp.length] ;
    for(int i = 0; i < matcher.length; i++) {
      matcher[i] = new RuleMatcher(mrFactory, exp[i]) ;
    }
  }

  public void defineMatch(String name, String exp) throws Exception {
    List<RuleMatcher> holder = new ArrayList<RuleMatcher>() ;
    if(matcher != null) {
      for(RuleMatcher sel : matcher) holder.add(sel) ;
    }
    RuleMatcher rmatcher = new RuleMatcher(mrFactory, exp) ;
    rmatcher.setName(name) ;
    holder.add(rmatcher) ;
    matcher = holder.toArray(new RuleMatcher[holder.size()]) ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    TokenCollection tokenSet = new TokenCollection(tokens) ;
    int pos = 0;
    while(pos < tokens.length) {
      RuleMatch rmatch = null ;
      for(RuleMatcher sel : matcher) {
        rmatch = sel.matches(tokenSet, pos) ;
        if(rmatch != null) break ;
      }
      if(rmatch != null) {
        onMatch(holder, rmatch.getRuleMatcher(), tokens, rmatch.getMatchFrom(), rmatch.getMatchTo()) ;
        pos = rmatch.getMatchTo() ;
      } else {
        holder.add(tokens[pos]) ;
        pos++ ;
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }

  protected IToken createChunking(IToken[] tokens, int from, int to) {
    if(to - from == 1) return tokens[from] ;
    TokenCollection collection = new TokenCollection(tokens, from, to) ;
    return collection ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    for(int i = from; i < to; i++) {
      holder.add(token[i]);
      System.out.println("match: " + token[i].getOriginalForm());
    }
  }
}
