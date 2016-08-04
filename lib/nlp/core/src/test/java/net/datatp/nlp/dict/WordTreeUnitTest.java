package net.datatp.nlp.dict;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.nlp.dict.LexiconDictionary;
import net.datatp.nlp.dict.Meaning;
import net.datatp.nlp.dict.WordTree;
import net.datatp.nlp.token.Token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeUnitTest {
  @Test
  public void test() throws Exception {
    String[] words = {"Ha Noi", "Viet Nam", "chien tranh", "chien tranh Viet Nam"} ;
    LexiconDictionary dict = new LexiconDictionary() ;
    for(String sel : words) {
      Meaning meaning = new Meaning() ;
      meaning.setOType("lexicon") ;
      meaning.setName(sel) ;
      dict.add(meaning) ;
    }
    WordTree wordTree = dict.getWordTree() ;
    wordTree.dump(System.out, "") ;

    String text = "cuoc chien tranh viet nam nam 72 tai Ha Noi" ;
    String[] word = text.split(" ") ;
    Token[] token = new Token[word.length] ;
    for(int i = 0; i < word.length; i++) token[i] = new Token(word[i]) ;
    Assert.assertEquals("chien tranh viet nam", wordTree.matches(token, 1).getEntry().getName()) ;
    Assert.assertEquals("ha noi", wordTree.matches(token, 8).getEntry().getName()) ;
  }
}