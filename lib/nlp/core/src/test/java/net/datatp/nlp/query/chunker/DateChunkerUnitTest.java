package net.datatp.nlp.query.chunker;

import org.junit.Before;
import org.junit.Test;

import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;

public class DateChunkerUnitTest {
  private WordTokenizerVerifier verifier ;
  private WordTokenizerVerifier wsverifier ;

  @Before
  public void setup() throws Exception {
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        //new DateTokenAnalyzer(), new TimeTokenAnalyzer(), 
        new DateChunker(),
    };
    verifier = new WordTokenizerVerifier(analyzer) ;
    //		LexiconDictionary dict = NLPResource.getInstance().getDictionary(LexiconDictionary.VI_LEXICON_RES) ;
    TokenAnalyzer[] wsanalyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        //			new WordTreeMatchingAnalyzer(dict),
        new DateChunker(),
    };
    wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
  }

  @Test
  public void test() throws Exception {
    //Slash/dash/dot and frequency
    verify("02/20/2012",    "02/20/2012{date}") ;
    verify("02-20-2012",    "02-20-2012{date}") ;
    verify("02.20.2012",    "02.20.2012{date}") ;

    // Vietnamese format
    // + Full format with weekday
    verify("Chủ nhật 12 tháng hai năm 2012", "Chủ nhật 12 tháng hai năm 2012{date}") ;
    // + Full format without weekday
    verify("Ngày 12 tháng hai năm 2012", "Ngày 12 tháng hai năm 2012{date}") ;
    // + Short format without weekday
    verify("Hanoi 12 tháng hai năm 2012", "Hanoi", "12 tháng hai năm 2012{date}") ;
    verify("01 January, 2012",  "01 January , 2012{date}") ;


    // English format
    // + Full format start with weekday
    verify("Thursday, February 02, 2012",  "Thursday , February 02 , 2012{date}") ;
    verify("Wed, February 1, 2012",  "Wed , February 1 , 2012{date}") ;
    // + Short format without weekday began start with month 
    verify("Feb 02, 2012",  "Feb 02 , 2012{date}") ;
    verify("February, 01 2012",  "February , 01 2012{date}") ;

    // General format
    // + Short format, only contains month and day/year
    verify("tháng 3, 2012", "tháng 3 , 2012{date}");
    verify("Feb 02",  "Feb 02{date}") ;
    verify("Feb 2012",  "Feb 2012{date}") ;
    verify("November, 2011", "November , 2011{date}");

    //Verify fail 
    //verify("Feb 10201",  "Feb 10201{date}") ;
  }

  private void verify(String text, String ... expect) throws Exception {
    verifier.verify(text, expect) ;
    wsverifier.verify(text, expect) ;
  }
}