package net.datatp.nlp.query.chunker;

import org.junit.Before;
import org.junit.Test;

import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;

public class CurrencyChunkerUnitTest {
  private WordTokenizerVerifier wsverifier ;

  @Before
  public void setup() throws Exception {
    TokenAnalyzer[] wsanalyzer = {
      PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
      new CurrencyChunker(new MatcherResourceFactory()),
    };
    wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
  }

  @Test
  public void test() throws Exception {
    //formats followed by an unit
    verify("1000 đồng",            "1000 đồng{currency}") ;
    verify("1000 usd",             "1000 usd{currency}") ;
    verify("4.199.000 đ",          "4.199.000 đ{currency}") ;
    verify("3-4 triệu",            "3-4 triệu{currency}") ;
    verify("1,2 triệu",            "1,2 triệu{currency}") ;
    verify("1,2 triệu đồng",       "1,2 triệu đồng{currency}") ;
    
    // formats followed by an unit in bracket
    verify( "1000(vnđ)",            "1000 ( vnđ ){currency}") ;
    verify( "1000 (usd)",           "1000 ( usd ){currency}") ;
    verify( "15.5(usd)",            "15.5 ( usd ){currency}") ;
    
    // formats contain an unit in the begin/middle/end of string
    verify( "1000vnd",              "1000vnd{currency}") ;
    verify( "usd1000",              "usd1000{currency}") ;
    verify( "1000$ $1000",          "1000${currency}", "$1000{currency}") ;
    verify( "1tr2",                 "1tr2{currency}") ;
    verify( "3triệu3",              "3triệu3{currency}") ;
    verify( "2.990.000đ",           "2.990.000đ{currency}") ;
    verify( "2100$/m2",             "2100$/m2{currency}") ;
    verify( "12.000đ/km",           "12.000đ/km{currency}") ;
    verify( "8triệu/tháng",         "8triệu/tháng{currency}") ;
   
    // formats start with digit, followed by units
    verify( "2 triệu 2",            "2 triệu 2{currency}") ;
    verify( "100 ngàn đồng",        "100 ngàn đồng{currency}") ;
    verify( "3 trăm tỷ đô",         "3 trăm tỷ đô{currency}") ;
    
    // formats start with digit, followed by units
    verify( "ba đô la",            "ba đô la{currency}") ;
    verify( "ba tỷ đô",            "ba tỷ đô{currency}") ;
  }

  private void verify(String text, String ... expect) throws Exception {
    wsverifier.verify(text, expect) ;
  }
}