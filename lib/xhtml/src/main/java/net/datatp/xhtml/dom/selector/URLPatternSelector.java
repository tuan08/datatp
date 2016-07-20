package net.datatp.xhtml.dom.selector;

import net.datatp.util.text.StringExpMatcher;
import net.datatp.xhtml.dom.TNode;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLPatternSelector implements Selector {
  private StringExpMatcher[] matcher ;

  public URLPatternSelector(String ... pattern) {
    matcher = new StringExpMatcher[pattern.length] ;
    for(int i = 0; i < pattern.length; i++) {
      matcher[i] = new StringExpMatcher(pattern[i].toLowerCase()) ;
    }
  }

  public boolean isSelected(TNode node) {
    String nodeName = node.getNodeName() ;
    if(!"a".equals(nodeName)) return false ;
    String url = node.getAttribute("href") ;
    if(url == null) return false; 
    for(StringExpMatcher sel : matcher) {
      if(sel.matches(url)) return true ;
    }
    return false ;
  }
}