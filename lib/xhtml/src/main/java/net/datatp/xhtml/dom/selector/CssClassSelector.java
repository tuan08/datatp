package net.datatp.xhtml.dom.selector;

import net.datatp.util.text.StringExpMatcher;
import net.datatp.xhtml.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CssClassSelector implements Selector {
  private StringExpMatcher[] matcher ;

  public CssClassSelector(String exp) {
    this.matcher = new StringExpMatcher[] { new StringExpMatcher(exp.toLowerCase()) } ;
  }

  public CssClassSelector(String[] exp) {
    this.matcher = new StringExpMatcher[exp.length] ;
    for(int i = 0; i < matcher.length; i++) {
      matcher[i] = new StringExpMatcher(exp[i].toLowerCase()) ;
    }
  }

  public boolean isSelected(TNode node) {
    String css = node.getCssClass() ;
    if(css == null) return false ;
    for(StringExpMatcher sel : matcher) {
      if(sel.matches(css)) return true ;
    }
    return false ;
  }
}