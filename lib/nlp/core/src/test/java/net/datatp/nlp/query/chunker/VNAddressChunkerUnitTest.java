package net.datatp.nlp.query.chunker;

import org.junit.Before;
import org.junit.Test;

import net.datatp.nlp.NLPResource;
import net.datatp.nlp.dict.LexiconDictionary;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.ws.WordTreeMatchingAnalyzer;

public class VNAddressChunkerUnitTest {
  private WordTokenizerVerifier wsverifier ;

  @Before
  public void setup() throws Exception {
    LexiconDictionary dict = NLPResource.getInstance().getLexiconDictionary(LexiconDictionary.VI_LEXICON_RES) ;
    TokenAnalyzer[] wsanalyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new WordTreeMatchingAnalyzer(dict), new VNAddressChunker(new MatcherResourceFactory()),
    };
    wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
  }

  @Test
  public void test() throws Exception {
    verify(
        "186 Trương Định, Hà Nội", 
        "186 Trương Định , Hà Nội{address}") ;
    verify(
        "186 Trương Định, Phường Trương Định, Quận Hai Bà Trưng, Hà Nội", 
        "186 Trương Định , Phường Trương Định , Quận Hai Bà Trưng , Hà Nội{address}") ;
    verify(
        "186 Trương Định, Phường Trương Định, Hà Nội", 
        "186 Trương Định , Phường Trương Định , Hà Nội{address}") ;
    verify(
        "186 Trương Định, Quận Hai Bà Trưng, Hà Nội", 
        "186 Trương Định , Quận Hai Bà Trưng , Hà Nội{address}") ;
    verify(
        "186 Trương Định, Quận Hai Bà Trưng", 
        "186 Trương Định , Quận Hai Bà Trưng{address}") ;

    verify("186 Trương Định",        "186 Trương Định{address}") ;
    verify("23/30 Trương Định",      "23/30 Trương Định{address}") ;
    verify("23n/30/100 Trương Định", "23n/30/100 Trương Định{address}") ;
  }

  private void verify(String text, String ... expect) throws Exception {
    wsverifier.verify(text, expect) ;
  }
}