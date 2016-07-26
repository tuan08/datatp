package net.datatp.nlp.token.tag;

import net.datatp.nlp.util.StringPool;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class TokenTag {
  public String getTagValue() { return null ; }

  abstract public String  getOType() ;
  abstract public boolean isTypeOf(String type) ;

  public String getInfo() {
    return getOType() + ": {" + getTagValue() + "}" ;
  }

  public void optimize(StringPool pool) {
  }
}