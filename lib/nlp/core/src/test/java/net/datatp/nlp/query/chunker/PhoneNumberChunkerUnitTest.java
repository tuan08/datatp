package net.datatp.nlp.query.chunker;

import org.junit.Before;
import org.junit.Test;

import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;

public class PhoneNumberChunkerUnitTest {
  private WordTokenizerVerifier wsverifier ;

  @Before
  public void setup() throws Exception {
    TokenAnalyzer[] wsanalyzer = {
      PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
      new PhoneNumberChunker(new MatcherResourceFactory()),
    };
    wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
  }

  @Test
  public void test() throws Exception {
    //Common format
    // + Regular format
    verify("0928996379",          "0928996379{phone}") ;
    verify("01686778225",         "01686778225{phone}") ;
    // + Format with dot
    verify("0928.996.379",        "0928.996.379{phone}");
    verify("0934.489768",         "0934.489768{phone}");
    verify("04.6277.1234",        "04.6277.1234{phone}");
    verify("84.4.37547.460",      "84.4.37547.460{phone}");
    verify("04.3.786.82.92",      "04.3.786.82.92{phone}");
    // + Format with dash
    verify("04-3736-6491",        "04-3736-6491{phone}");
    verify("04-37831999",         "04-37831999{phone}");
    //verify("0979789067-0905921227",         "0979789067-0905921227{phone}");
    
    // The format start with bracket followed by two or three frequency groups
    verify("(08) 3855 6666",      "( 08 ) 3855 6666{phone}");
    verify("(+84) 983 870 204",   "( +84 ) 983 870 204{phone}");
    verify("(+84) 4 3754 8864",   "( +84 ) 4 3754 8864{phone}");
    
    // The format start with bracket followed by a common format
    verify("(+84) 983.870.205",   "( +84 ) 983.870.205{phone}");
    verify("(+84) 4.37.54.78.13", "( +84 ) 4.37.54.78.13{phone}");
    
    // The format contains spaces between frequency groups
    verify("04 3754 8864",  "04 3754 8864{phone}");
    verify("0928 996 379",  "0928 996 379{phone}") ;
    verify("0164 203 5555", "0164 203 5555{phone}") ;
    verify("0904 098777",   "0904 098777{phone}") ;
    
    //not phone frequency
    verifyFail("100.000",             "100.000{phone}") ;
    verifyFail("123",                 "123{phone}") ;
    verifyFail("1234567",             "1234567{phone}") ;
    verifyFail("1",                   "1{phone}") ;
  }

  private void verify(String text, String ... expect) throws Exception {
    wsverifier.verify(text, expect) ;
  }
  
  private void verifyFail(String text, String ... expect) throws Exception {
    wsverifier.verifyFail(text, expect) ;
  }
}