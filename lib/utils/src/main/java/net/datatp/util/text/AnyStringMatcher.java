package net.datatp.util.text;

import java.util.HashSet;

public class AnyStringMatcher {
  private HashSet<String> matchers;
  private int             minLength = Integer.MAX_VALUE;
  private int             maxLength = 0;
  
  public AnyStringMatcher(String[] exp) {
    this(exp, false);
  }
  
  public AnyStringMatcher(String[] string, boolean normalize) {
    matchers = new HashSet<>();
    for(int i = 0; i < string.length; i++) {
      if(normalize) string[i] = string[i].toLowerCase().trim();
      matchers.add(string[i]);
      int length = string[i].length();
      if(length < minLength) minLength = length;
      if(length > maxLength) maxLength = length;
    }
  }
  
  public boolean matches(String string) {
    int length = string.length();
    if(length < minLength || length > maxLength) return false;
    return matchers.contains(string); 
  }
}
