package net.datatp.xhtml.dom.selector;

import net.datatp.util.text.StringExpMatcher;
import net.datatp.xhtml.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ElementIdSelector implements Selector {
  private StringExpMatcher[] matcher ;

  public ElementIdSelector(String exp) {
    this.matcher = new StringExpMatcher[] { new StringExpMatcher(exp.toLowerCase()) } ;
  }

  public ElementIdSelector(String[] exp) {
    this.matcher = new StringExpMatcher[exp.length] ;
    for(int i = 0; i < matcher.length; i++) {
      matcher[i] = new StringExpMatcher(exp[i].toLowerCase()) ;
    }
  }

  public boolean isSelected(TNode node) {
    String eleId = node.getElementId() ;
    if(eleId == null) return false ;
    for(StringExpMatcher sel : matcher) {
      if(sel.matches(eleId)) return true ;
    }
    return false ;
  }
}