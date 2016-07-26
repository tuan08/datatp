package net.datatp.nlp.query.match;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.PhraseIterator;
import net.datatp.nlp.token.SentenceTokenizer;
import net.datatp.nlp.token.SingleSequenceIterator;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenSequenceIterator;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class RuleMatcher {
  final static public Pattern DISTANCE_PATTERN = Pattern.compile("\\.\\d*\\.") ;
  final static public int     ANY_DISTANCE     = 100000000 ;
  final static public byte    PHRASE = 1, SENTENCE = 2, LINE = 3, BLOCK = 4, DOCUMENT = 5 ;

  private String        name ;
  private String        ruleExp ;
  private byte          matchScope ;
  private String        fieldName ;
  private UnitMatcher[] unitMatcher ;

  public RuleMatcher(MatcherResourceFactory umFactory, String exp) throws Exception {
    this.ruleExp = exp ;
    exp = exp.trim() ;
    int idx = exp.indexOf(" ") ;
    String match = exp.substring(0, idx) ;
    exp = exp.substring(idx + 1).trim() ;
    if(match.startsWith("/p")) {
      this.matchScope = PHRASE ;
    } else if(match.startsWith("/s")) {
      this.matchScope = SENTENCE ;
    } else if(match.startsWith("/l")) {
      this.matchScope = LINE ;
    } else if(match.startsWith("/b")) {
      this.matchScope = BLOCK ;
    } else {
      this.matchScope = DOCUMENT ;
    }

    if(match.length() > 2 && match.endsWith("]")) {
      idx = match.indexOf("[") ;
      this.fieldName = match.substring(idx + 1, match.length() - 1) ;
    }

    List<UnitMatcher> list = new ArrayList<UnitMatcher>() ;

    idx = 0 ;
    int startUnit = 0 ;
    Matcher dmatcher = DISTANCE_PATTERN.matcher(exp) ;
    while(idx >= 0) {
      if(dmatcher.find(idx)) {
        idx = dmatcher.start() ;
        String unitMatcherExp = exp.substring(startUnit, idx) ;
        String distance = exp.substring(idx + 1, dmatcher.end() - 1) ;
        list.add(umFactory.create(unitMatcherExp, distance)) ;
        startUnit = dmatcher.end() ;
        idx = startUnit ;
      } else {
        idx = -1 ;
      }
    }
    String unitMatcherExp = exp.substring(startUnit, exp.length()) ;
    list.add(umFactory.create(unitMatcherExp, null)) ;
    this.unitMatcher = list.toArray(new UnitMatcher[list.size()]) ;
  }

  public String getName() { return name ; }
  public void   setName(String name) { this.name = name ; } 

  public String getRuleExp() { return this.ruleExp ; }

  final public RuleMatch[] matches(QueryDocument doc, int maxReturn) throws Exception {
    List<RuleMatch> holder = new ArrayList<RuleMatch>() ;
    matches(holder, doc, maxReturn) ;
    if(holder.size() == 0) return null ;
    return holder.toArray(new RuleMatch[holder.size()]) ;
  }

  final public void matches(List<RuleMatch> holder, QueryDocument doc, int maxReturn) throws Exception {
    if(fieldName == null) {
      matchDoc(holder, doc, maxReturn) ;
    } else {
      TokenCollection field = doc.getDocumentField(fieldName) ;
      if(field != null) matchField(holder, field, maxReturn) ;
    }
  }

  private void matchDoc(List<RuleMatch> holder, QueryDocument doc, int maxReturn) throws Exception {
    Map<String, TokenCollection> fields = doc.getDocumentFields() ;
    Iterator<TokenCollection> i = fields.values().iterator() ;
    while(i.hasNext()) {
      TokenCollection field = i.next() ;
      matchField(holder, field, maxReturn) ;
      if(holder.size() == maxReturn) return ;
    }
  }

  private void matchField(List<RuleMatch> holder, TokenCollection collection, int maxReturn) throws Exception {
    TokenSequenceIterator tokensetIterator = null ;
    if(this.matchScope == PHRASE) {
      tokensetIterator = new PhraseIterator(collection.getTokens()) ;
    } else if(this.matchScope == SENTENCE) {
      tokensetIterator = new SentenceTokenizer(collection.getTokens()) ;
    } else if(this.matchScope == LINE) {
    } else if(this.matchScope == BLOCK) {
      tokensetIterator = null ;
    } else {
      tokensetIterator = new SingleSequenceIterator(collection.getTokens()) ;
    }
    TokenCollection tokenSet = null ;
    while((tokenSet = tokensetIterator.next()) != null) {
      RuleMatch ruleMatch = matches(tokenSet) ;
      if(ruleMatch != null) {
        holder.add(ruleMatch) ;
        if(holder.size() == maxReturn) return ;
      }
    }
  }

  public RuleMatch matches(TokenCollection tokenSet) {
    IToken[] tokens = tokenSet.getTokens() ;
    for(int i = 0; i < tokens.length; i++) {
      RuleMatch rmatch = this.matches(tokenSet, i) ;
      if(rmatch != null) return rmatch ;
    }
    return null ;
  }

  public RuleMatch matches(TokenCollection tokenSet, int from) {
    IToken[] tokens = tokenSet.getTokens() ;
    UnitMatch unitMatch = unitMatcher[0].matches(tokens, from) ;
    if(unitMatch != null) {
      RuleMatch ruleMatch = new RuleMatch(this, new TokenCollection(tokens)) ;
      ruleMatch.add(unitMatch) ;
      if(unitMatcher.length ==  1) return ruleMatch;
      int maxDistance = unitMatcher[0].getAllowNextMatchDistance() ;
      int distance = 0 ;
      int currentMatcher = 1 ;
      int pos = unitMatch.getTo() ;
      while(pos < tokens.length) {
        try{
          unitMatch = unitMatcher[currentMatcher].matches(tokens, pos) ;
          if(unitMatch != null) {
            ruleMatch.add(unitMatch) ;
            maxDistance = unitMatcher[currentMatcher].getAllowNextMatchDistance() ;
            distance = 0 ;
            currentMatcher++ ;
            if(currentMatcher == unitMatcher.length) return ruleMatch;
            pos = unitMatch.getTo() ;
          } else {
            distance++ ;
            if(distance > maxDistance) break ;
            pos++ ;
          }
        } catch(Throwable ex) {
          StringBuilder b2 = new StringBuilder() ;
          b2.append("Match Rule:\n");
          b2.append("     ").append(toString()).append("\n");
          b2.append("Match:\n");
          b2.append("     ").append(tokenSet.getOriginalForm());
          //FileUtil.writeToFile("d://bug.out.txt", b2.toString().getBytes("UTF-8"), true) ;
          System.out.println(b2.toString());
          throw new RuntimeException(ex) ;
        }
      }
    }
    return null ;
  }
}