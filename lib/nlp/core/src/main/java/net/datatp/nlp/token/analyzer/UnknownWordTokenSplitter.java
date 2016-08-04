package net.datatp.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.NLP;
import net.datatp.nlp.dict.LexiconDictionary;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.MeaningTag;
import net.datatp.nlp.ws.WordTreeMatchingAnalyzer;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnknownWordTokenSplitter extends TokenAnalyzer {
  private TokenAnalyzer splitAnalyzer ;

  public UnknownWordTokenSplitter() { }
  
  public UnknownWordTokenSplitter(LexiconDictionary dict) {
    splitAnalyzer = new ChainTokenAnalyzer(new CommonTokenAnalyzer(), new WordTreeMatchingAnalyzer(dict)) ;
  }
  
  public void configure(NLP nlp) {
    LexiconDictionary dict = nlp.getLexiconDictionary();
    splitAnalyzer = new ChainTokenAnalyzer(new CommonTokenAnalyzer(), new WordTreeMatchingAnalyzer(dict)) ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    for(int i = 0; i < tokens.length; i++) {
      MeaningTag ltag = tokens[i].getFirstTagType(MeaningTag.class) ;
      if(ltag != null || tokens[i].getWord().length == 1) {
        holder.add(tokens[i]) ;
      } else {
        if(tokens[i] instanceof TokenCollection) {
          holder.add(tokens[i]) ;
        } else {
          split(holder, tokens[i]) ;
        }
      }
    }
    return holder.toArray(new IToken[holder.size()]) ;
  }

  void split(List<IToken> holder, IToken token) throws TokenException {
    List<String> oword = StringUtil.split(token.getOriginalForm(), ' ') ;
    int icap = 0 ;
    for(int i = 0; i < oword.size(); i++) {
      String word = oword.get(i) ;
      if(word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
        icap++  ;
      }
    }

    if(icap == oword.size()) {
      holder.add(token) ;
      return ;
    }

    IToken[] splitTokens = new IToken[oword.size()] ;
    for(int i = 0; i < splitTokens.length; i++) {
      splitTokens[i] = new Token(oword.get(i)) ;
    }
    splitTokens = splitAnalyzer.analyze(splitTokens) ;
    for(IToken sel : splitTokens) holder.add(sel) ;
  }
}