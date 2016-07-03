package net.datatp.xhtml.dom.selector;

import net.datatp.util.text.StringMatcher;
import net.datatp.xhtml.dom.TNode;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLPatternSelector implements Selector {
  private StringMatcher[] matcher ;

  public URLPatternSelector(String ... pattern) {
    matcher = new StringMatcher[pattern.length] ;
    for(int i = 0; i < pattern.length; i++) {
      matcher[i] = new StringMatcher(pattern[i].toLowerCase()) ;
    }
  }

  public boolean isSelected(TNode node) {
    String nodeName = node.getNodeName() ;
    if(!"a".equals(nodeName)) return false ;
    String url = node.getAttribute("href") ;
    if(url == null) return false; 
    for(StringMatcher sel : matcher) {
      if(sel.matches(url)) return true ;
    }
    return false ;
  }
}