package net.datatp.nlp.ws;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.NLPResource;
import net.datatp.nlp.dict.Dictionary;
import net.datatp.nlp.dict.Entry;
import net.datatp.nlp.dict.WordTree;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.tag.PunctuationTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeMatchingAnalyzer implements TokenAnalyzer {
  private WordTree wtreeRoot ;

  public WordTreeMatchingAnalyzer() throws Exception {
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    wtreeRoot = dict.getWordTree() ;
  }

  public WordTreeMatchingAnalyzer(Dictionary dict) {
    wtreeRoot = dict.getWordTree() ;
  }

  public WordTreeMatchingAnalyzer(WordTree wtree) {
    wtreeRoot = wtree ;
  }

  public IToken[] analyze(IToken[] tokens)  {
    List<IToken> newList = new ArrayList<IToken>() ;
    int position = 0 ;
    while(position < tokens.length) {
      IToken token = tokens[position] ;
      if(token.hasTagType(PunctuationTag.TYPE)){
        newList.add(token) ;
        position++ ;
        continue ;
      } 
      WordTree foundTree = wtreeRoot.matches(tokens, position) ;
      if(foundTree != null) {
        Entry entry = foundTree.getEntry() ;
        int newPosition = position + entry.getWord().length ;
        Token newToken = new Token(tokens, position, newPosition) ;
        newToken.add(entry.getTag()) ;
        newList.add(newToken) ;
        position = newPosition ;
      } else {
        newList.add(token) ;
        position++ ;
      }
    }
    return newList.toArray(new Token[newList.size()]) ;
  }
}