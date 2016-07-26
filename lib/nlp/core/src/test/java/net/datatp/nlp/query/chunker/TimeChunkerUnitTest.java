package net.datatp.nlp.query.chunker;

import org.junit.Test;

import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.ws.NGramStatisticWSTokenAnalyzer;

public class TimeChunkerUnitTest {
  @Test
  public void test() throws Exception {
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new TimeChunker(),
        new NGramStatisticWSTokenAnalyzer()
    };
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    // Regular time format
    verifier.verify("00:36",                 "00:36{time}") ;
    verifier.verify("00:36am",               "00:36am{time}") ;
    verifier.verify("00:36pm",               "00:36pm{time}") ;
    verifier.verify("00:36:00",              "00:36:00{time}") ;
    verifier.verify("00:36:00am",            "00:36:00am{time}") ;
    verifier.verify("00:36:00pm",            "00:36:00pm{time}") ;

    // The format end with GMT/UTC
    verifier.verify("07:00 GMT",             "07:00 GMT{time}") ;
    verifier.verify("07:00 GMT+7",           "07:00 GMT+7{time}") ;
    verifier.verify("07:00 UTC",             "07:00 UTC{time}") ;
    verifier.verify("07:00 UTC-7:00",        "07:00 UTC-7:00{time}") ;

    // The format end with GMT/UTC in bracket
    verifier.verify("07:00 (GMT+7)",         "07:00 ( GMT+7 ){time}") ;
    verifier.verify("14:20(GMT+7)",          "14:20 ( GMT+7 ){time}") ;
    verifier.verify("07:00 GMT-07",          "07:00 GMT-07{time}") ;
    verifier.verify("07:00 (GMT-07)",        "07:00 ( GMT-07 ){time}") ;
    verifier.verify("07:00 AM (GMT+7)",      "07:00 AM ( GMT+7 ){time}") ;
    verifier.verify("07:00 PM (GMT-07:00)",  "07:00 PM ( GMT-07:00 ){time}") ;
    verifier.verify("06:02:00 AM (GMT-07)",  "06:02:00 AM ( GMT-07 ){time}") ;
    verifier.verify("07:00 (UTC-7:00)",      "07:00 ( UTC-7:00 ){time}") ;
    verifier.verify("07:00 UTC-07:00",       "07:00 UTC-07:00{time}") ;
    verifier.verify("07:00 (UTC-07:00)",     "07:00 ( UTC-07:00 ){time}") ;
  }
}