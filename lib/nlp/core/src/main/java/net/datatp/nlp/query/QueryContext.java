package net.datatp.nlp.query;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.datatp.nlp.query.match.RuleMatch;
import net.datatp.util.text.StringUtil;

public class QueryContext implements Serializable {
  private static final long serialVersionUID = 1L;
  static Map<String, String[]> EMPTY = Collections.unmodifiableMap(new HashMap<String, String[]>()) ;

  private boolean complete = false ;
  private boolean isContinue = false ;
  private RuleMatch[] ruleMatch ;
  private HashSet<String> tags = new HashSet<String>() ;
  private boolean debug = false ;

  public QueryContext() {}

  public boolean isComplete() { return complete ; }
  public void    setComplete(boolean b) { this.complete = b ; }

  public boolean isContinue() { return isContinue ; }
  public void    setContinue(boolean isContinue) { this.isContinue = isContinue ; }

  public RuleMatch[] getRuleMatch() { return this.ruleMatch ; }
  public void setRuleMatch(RuleMatch[] ruleMatch) { this.ruleMatch = ruleMatch ; }

  public boolean hasTag(String tag) { return tags.contains(tag) ; }
  public Set<String> getTags() { return tags ; }
  public void setTag(String tag) { tags.add(tag) ; }

  public boolean isDebug̣̣̣() { return debug ; }
  public void    setDebug(boolean b) { debug = b  ; } 

  public void dump() {
    System.out.println("Rule Matches: ");
    for(RuleMatch sel : ruleMatch) {
      System.out.println("  " + sel.getRuleMatcher().getRuleExp()) ;
      System.out.println("    " + sel.getTokenCollection().getOriginalForm()) ;
      System.out.println("    " + sel.getUnitMatchString()) ;
      System.out.println(         sel.getExtractString("    ")) ;
    }
    System.out.println("  Tags: " + StringUtil.join(tags, ", ")) ;
  }
}