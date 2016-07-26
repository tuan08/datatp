package net.datatp.nlp.token.tag;

import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PunctuationTag extends TokenTag {
  final static public String TYPE = "punctuation" ;

  static char[] PUNC_CHARACTER = CharacterSet.merge(CharacterSet.PUNCTUATION) ;

  final static public PunctuationTag INSTANCE = new PunctuationTag() ;

  final static public boolean isPunctuation(char c) {
    return CharacterSet.isIn(c, PUNC_CHARACTER) ;
  }

  public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) {
    return TYPE.equals(type);
  }
}