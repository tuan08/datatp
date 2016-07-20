package net.datatp.util.text;

public class AnyStringExpMatcher {
  private StringExpMatcher[] matcher;
  
  public AnyStringExpMatcher(String[] exp) {
    matcher = StringExpMatcher.create(exp);
  }
  
  public AnyStringExpMatcher(String[] exp, boolean normalize) {
    for(int i = 0; i < exp.length; i++) {
      exp[i] = exp[i].toLowerCase().trim();
    }
    matcher = StringExpMatcher.create(exp);
  }
  
  public boolean matches(String string) {
    for(StringExpMatcher sel : matcher) {
      if(sel.matches(string)) return true ;
    }
    return false; 
  }
}
