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

public class HintEntityChunkerUnitTest {
  private WordTokenizerVerifier wsverifier ;

  @Before
  public void setup() throws Exception {
    LexiconDictionary dict = NLPResource.getInstance().getLexiconDictionary(LexiconDictionary.VI_LEXICON_RES) ;
    TokenAnalyzer[] wsanalyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new WordTreeMatchingAnalyzer(dict),
        new HintEntityChunker(new MatcherResourceFactory()),
    };
    wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
  }

  @Test
  public void testCase() throws Exception {
    verify("ông Trần Hưng Đạo", "ông", "Trần Hưng Đạo{person}") ;
    verify("phố Trần Hưng Đạo", "phố", "Trần Hưng Đạo{location}") ;
  }

  private void verify(String text, String ... expect) throws Exception {
    wsverifier.verify(text, expect) ;
  }
}