package net.datatp.nlp.token.analyzer;

import net.datatp.nlp.dict.Dictionary;
import net.datatp.nlp.dict.Entry;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.WordTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictionaryTaggingAnalyzer implements TokenAnalyzer {
  private Dictionary dict ;

  public DictionaryTaggingAnalyzer(Dictionary dict) {
    this.dict = dict ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    for(int i = 0; i < tokens.length; i++) {
      Entry entry = dict.getEntry(tokens[i].getNormalizeForm()) ;
      if(entry != null) {
        tokens[i].removeTagType(WordTag.class) ;
        tokens[i].add(entry.getTag()) ;
      }
    }
    return tokens ;
  }
}