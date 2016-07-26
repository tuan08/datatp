package net.datatp.nlp.query.match;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.nlp.query.QueryDocument;
import net.datatp.nlp.token.TokenCollection;

public class RuleMatchers {
  private int matchmax ;
  private RuleMatcher[] rules ;

  public RuleMatchers(MatcherResourceFactory factory, String[] exp, int matchmax) throws Exception {
    this.matchmax = matchmax ;
    this.rules = new RuleMatcher[exp.length] ;
    for(int i = 0; i < rules.length; i++) {
      this.rules[i] = new RuleMatcher(factory, exp[i]) ;
    }
  }

  final public RuleMatch[] matches(TokenCollection doc) throws Exception {
    List<RuleMatch> holder = new ArrayList<RuleMatch>() ;
    for(int i = 0; i < rules.length; i++) {
      RuleMatch ruleMatch = rules[i].matches(doc) ;
      if(ruleMatch != null) holder.add(ruleMatch) ;
    }
    return removeDuplicate(holder);
  }

  final public RuleMatch[] matches(QueryDocument doc) throws Exception {
    List<RuleMatch> holder = new ArrayList<RuleMatch>() ;
    for(int i = 0; i < rules.length; i++) {
      RuleMatch[] ruleMatch = rules[i].matches(doc, matchmax) ;
      if(ruleMatch != null) {
        for(RuleMatch sel : ruleMatch) holder.add(sel) ;
      }
    }
    return removeDuplicate(holder);
  }

  final public RuleMatch[] removeDuplicate(List<RuleMatch> holder) throws Exception {
    if(holder.size() == 0) return null ;
    if(holder.size() > 1) {
      Map<String, RuleMatch> map = new LinkedHashMap<String, RuleMatch>() ;
      for(int i = 0; i < holder.size(); i++) {
        RuleMatch sel = holder.get(i) ;
        String text = sel.getTokenCollection().getNormalizeForm() ;
        if(map.containsKey(text)) continue ;
        map.put(text, sel) ;
      }
      return map.values().toArray(new RuleMatch[map.size()]) ;
    } else {
      return holder.toArray(new RuleMatch[holder.size()]) ;
    }
  }
}