package net.datatp.nlp.query.match;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.nlp.token.TokenCollection;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class RuleMatch {
  private RuleMatcher ruleMatcher ;
  private TokenCollection tokenCollection ;
  private List<UnitMatch> unitMatchHolder = new ArrayList<UnitMatch>() ;
  private LinkedHashMap<String, String[]> extracts = new LinkedHashMap<String, String[]>() ;

  public RuleMatch(RuleMatcher ruleMatcher, TokenCollection tokenCollection) {
    this.ruleMatcher = ruleMatcher ;
    this.tokenCollection = tokenCollection ;
  }

  public RuleMatcher getRuleMatcher() { return this.ruleMatcher ; }

  public TokenCollection getTokenCollection() { return tokenCollection ; }
  public void setTokenCollection(TokenCollection collection) { this.tokenCollection = collection ; }

  public List<UnitMatch> getUnitMatch() { return this.unitMatchHolder ; }

  public void add(UnitMatch unitMatch) { unitMatchHolder.add(unitMatch) ; }

  public UnitMatch getUnitMatch(String word){
    String[] keywords = extracts.get(word);
    if(keywords == null) return null;
    String keyword = keywords[0];
    for(UnitMatch uMatch: unitMatchHolder) 
      if(keyword.equals(uMatch.getWord())) return uMatch;
    return null;
  }

  public int getMatchFrom() {
    if(unitMatchHolder.size() == 0) return -1 ;
    UnitMatch first = unitMatchHolder.get(0) ;
    return first.getFrom() ;
  }

  public int getMatchTo() {
    if(unitMatchHolder.size() == 0) return -1 ;
    UnitMatch last = unitMatchHolder.get(unitMatchHolder.size() - 1) ;
    return last.getTo() ;
  }

  public void clear() { this.unitMatchHolder.clear() ; }

  public Map<String, String[]> getExtracts(){ return this.extracts; }

  public String[] getExtract(String name) {
    return extracts.get(name) ;
  }

  public void setExtract(String key, String value) {
    extracts.put(key, new String[] { value }) ;
  }

  public void setExtract(String key, String[] value) {
    extracts.put(key, value) ;
  }

  public void addExtract(String key, String value) {
    String[] exist = extracts.get(key) ;
    if(exist != null) {
      exist = StringUtil.merge(exist, value);
      extracts.put(key, exist) ;
    } else {
      extracts.put(key, exist) ;
    }
  }


  public String getUnitMatchString() {
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < unitMatchHolder.size(); i++) {
      if(i > 0) b.append(" .. ") ;
      UnitMatch unitMatch = unitMatchHolder.get(i) ;
      b.append(unitMatch) ;
    } 
    return b.toString() ;
  }

  public String getExtractString(String indent) {
    StringBuilder b = new StringBuilder() ;
    Iterator<Map.Entry<String, String[]>> i = this.extracts.entrySet().iterator() ;
    boolean first = true ;
    while(i.hasNext()) {
      if(!first) b.append("\n") ;
      Map.Entry<String, String[]> entry = i.next() ;
      b.append(indent).append(entry.getKey()).append(" = ").append(StringUtil.joinStringArray(entry.getValue())) ;
      first = false ;
    }
    return b.toString() ;
  }

  public String toString() { return getUnitMatchString() ; }
}