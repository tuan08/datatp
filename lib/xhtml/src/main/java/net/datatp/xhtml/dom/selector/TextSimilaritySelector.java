package net.datatp.xhtml.dom.selector;

import java.util.HashSet;
import java.util.Set;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.TNode;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextSimilaritySelector implements Selector {
  private Set<String> set ;
  private int minLength = 100000, maxLength ;

  public TextSimilaritySelector(String[] text) {
    set = new HashSet<String>() ;
    for(int i = 0; i < text.length; i++) {
      set.add(normalize(text[i])) ;
      if(text[i].length() < minLength) minLength = text[i].length() ;
      if(text[i].length() > maxLength) maxLength = text[i].length() ;
    }
  }

  public boolean isSelected(TNode node) {
    String ntext = node.getNodeValue() ;
    if(StringUtil.isEmpty(ntext)) return false ;
    if(ntext.length() < minLength || ntext.length() > maxLength) return false ;
    ntext = normalize(ntext) ;
    return set.contains(ntext) ;
  }

  static public String normalize(String label) {
    if(label == null || label.length() == 0) return label ;
    StringBuilder b = new StringBuilder() ;
    char[] buf = label.toCharArray() ;
    for(char c : buf) {
      if(Character.isLetter(c)) b.append(Character.toLowerCase(c)) ;
    }
    return b.toString() ;
  }
}