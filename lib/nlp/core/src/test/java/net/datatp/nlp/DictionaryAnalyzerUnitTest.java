package net.datatp.nlp;

import org.junit.Test;

import net.datatp.nlp.dict.Dictionary;
import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.ws.WordTreeMatchingAnalyzer;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictionaryAnalyzerUnitTest {
  @Test
  public void testLongestMatchingAnalyzer() throws Exception {
    String[] dictRes = {
        "classpath:nlp/vn.lexicon.json",
        "classpath:nlp/lienhe.synset.json",
        "classpath:nlp/entity/vn.place.json",
        "classpath:nlp/mobile.product.json"
    };
    Dictionary dict = new Dictionary(dictRes) ;
    TokenAnalyzer[] analyzer = {
      new CommonTokenAnalyzer(), new PunctuationTokenAnalyzer(), 
      new GroupTokenMergerAnalyzer(), new WordTreeMatchingAnalyzer(dict)
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify("Một quả bom sắp nổ.", "Một", "quả bom{postag, synset}", "sắp", "nổ", ".") ;
    verifier.verify("186 trương định", "186", "trương định{entity}") ;
    verifier.verify("mua iphone",  "mua", "iphone{product}") ;
  }
}
