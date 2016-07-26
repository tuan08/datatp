package net.datatp.nlp.util;

import java.util.HashMap;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class StringPool extends HashMap<String, String> {
  public String getString(String value) {
    if(value == null) return null ;
    String ret = get(value) ;
    if(ret != null) return ret ;
    put(value, value) ;
    return value ;
  }

  public String[] getString(String[] value) {
    if(value == null) return null ;
    for(int i = 0; i < value.length; i++) {
      String ret = get(value[i]) ;
      if(ret != null) {
        value[i] = ret ;
      } else {
        put(value[i], value[i]) ;
      }
    }
    return value ;
  }
}