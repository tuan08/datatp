package net.datatp.nlp.query.match;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.datatp.nlp.token.IToken;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TreeWordMatcher extends UnitMatcher {
  private String word ;
  private Map<String, TreeWordMatcher> trees = new HashMap<String, TreeWordMatcher>(5) ;

  public TreeWordMatcher() {

  }

  public TreeWordMatcher(String[] word) {
    addWord(word) ;
  }

  public UnitMatcher init(ParamHolder holder, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    return this ;
  }

  public String getWord() { return this.word ; }
  public void setWord(String word) { this.word = word ; }

  public void addWord(String word) {
    String nword = word.toLowerCase() ;
    String[] token = nword.split(" ") ;
    add(token, 0) ;
    add(new String[] { nword }, 0) ;
  }

  public void addWord(String[] word) {
    if(word == null) return ;
    for(int i = 0 ; i < word.length; i++) {
      String nword = word[i].toLowerCase() ;
      String[] token = nword.split(" ") ;
      add(token, 0) ;
      add(new String[] { nword }, 0) ;
      //System.out.println(nword);
    }
  }

  public void add(String[] token, int pos) {
    TreeWordMatcher twm = trees.get(token[pos]) ;
    if(twm == null) {
      twm = new TreeWordMatcher() ;
      trees.put(token[pos], twm) ;
    }
    if(pos + 1 == token.length) {
      twm.setWord(StringUtil.joinStringArray(token, " ")) ;
    }
    if(pos + 1 < token.length) {
      twm.add(token, pos + 1) ;
    }
  }

  public UnitMatch matches(IToken[] token, int pos) {
    TreeWordMatcher twm = trees.get(token[pos].getNormalizeForm()) ;
    if(twm == null) {
      return null ;
    } 

    UnitMatch unitMatch = null ;
    if(pos + 1 < token.length) {
      unitMatch = twm.matches(token, pos + 1) ;
    }
    if(unitMatch == null && twm.getWord() != null) {
      unitMatch = new UnitMatch(twm.getWord(), pos, pos + 1) ;
    }

    if(unitMatch != null) {
      unitMatch.setFrom(pos) ;
      unitMatch.setUnitMatcher(this) ;
    }
    return unitMatch ;
  }

  public void dump(int level) {
    Iterator<Map.Entry<String, TreeWordMatcher>> i = trees.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, TreeWordMatcher>  entry = i.next() ;
      for(int j = 0; j < level; j++) {
        System.out.print(' ');
      }
      String key = entry.getKey() ;
      TreeWordMatcher value = entry.getValue() ;
      System.out.append(key) ;
      if(value.getWord() != null) {
        System.out.append('[').append(value.getWord()).append(']') ;
      }
      System.out.append('\n') ;
      value.dump(level + 2) ;
    }
  }
}