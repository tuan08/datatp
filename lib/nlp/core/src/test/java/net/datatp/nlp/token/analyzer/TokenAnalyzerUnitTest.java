package net.datatp.nlp.token.analyzer;

import org.junit.Test;

import net.datatp.nlp.token.WordTokenizerVerifier;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNMobileTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNNameTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNPhoneTokenAnalyzer;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenAnalyzerUnitTest {
  @Test
  public void tesṭ̣̣̣̣CommonTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = { new CommonTokenAnalyzer() } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "1234  seqletter A4 A4-A5 1.234", 
        "1234{digit}", "seqletter{letter}", "A4{character}", "A4-A5{character}", "1.234{frequency}") ;
    verifier.verify(
        "1.2 1,2 1.200.000,00 1,200,000.00", 
        "1.2{frequency}", "1,2{frequency}", "1.200.000,00{frequency}", "1,200,000.00{frequency}") ;
    //TODO: Continuous numbers counted
    //		verifier.verify(
    //		    "2, 3 mans",
    //		    "2{frequency}", "3{frequency}", "mans{letter}");
    verifier.verify(
        "test ?.? token", 
        "test", "?.?", "token") ;
  }

  @Test
  public void tesṭ̣̣̣̣GroupTokenMergerAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify("666 777 888 999", "666 777 888 999") ;
  }

  @Test
  public void tesṭ̣̣̣̣PunctuationTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new PunctuationTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "Mr. <test> test the PunctuationAnalyzer.", 
        "Mr.", "<", "test", ">{punctuation}", "test", "the", "PunctuationAnalyzer", ".{punctuation}") ;
    //TODO: Abbreviated names
    //		verifier.verify(
    //		   "Mr. St. Louis was here in 1980.",
    //		   "Mr.", "St.", "Louis", "was", "here", "in", "1980", ".{punctuation}");
    verifier.verify(
        "Mr. test test the PunctuationAnalyzer? ", 
        "Mr.", "test", "test", "the", "PunctuationAnalyzer", "?{punctuation}") ;
  }

  @Test
  public void tesṭ̣̣̣̣DateTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new DateTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "12/2/2010 12.2.2010 12-2-2010 2010/2/12", 
        "12/2/2010{date}", "12.2.2010{date}", "12-2-2010{date}", "2010/2/12{date}") ;
    // TODO: check valid date
    //		verifier.verify(
    //		   "12/32/2001 12.32.2001 12-32-2001",
    //		   "12/32/2001{date}", "12.32.2001{date}", "12-32-2001{date}");
  }

  @Test
  public void tesṭ̣̣̣̣EmailTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), PunctuationTokenAnalyzer.INSTANCE, new GroupTokenMergerAnalyzer(), new EmailTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "tuan.nguyen@headvances.com tuan.nguyen@localhost admin@localhost", 
        "tuan.nguyen@headvances.com{email}", "tuan.nguyen@localhost{email}", "admin@localhost{email}") ;
    // TODO: It can be occurred in blog's/forum's content. It's similar with "PS.", but to a specific people.
    verifier.verify(
        "@Thanh: This is a test",
        "@Thanh", ":", "This", "is", "a", "test");
  }

  @Test
  public void tesṭ̣̣̣̣TimeTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new TimeTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "12:00 12:00am 12:00:00am", 
        "12:00{time}", "12:00am{time}", "12:00:00am{time}") ;
    verifier.verify(
        "2:00 pm 2:00:00 pm", 
        "2:00{time}", "pm", "2:00:00{time}", "pm") ;
    // TODO: What's about "2am or 2 pm"?
    //		verifier.verify(
    //		    "2 pm", 
    //		    "2{time}", "pm");
  }

  @Test
  public void tesṭ̣̣̣̣USDTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new USDTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "1000 USD 1000 (USD) 15.5(USD)", 
        "1000{currency}", "USD", "1000{currency}", "(", "USD", ")", "15.5{currency}", "(",  "USD", ")") ;
    verifier.verify(
        "usd1000", 
        "usd1000{currency}") ;
    verifier.verify(
        "1000$ $1000", 
        "1000${currency}", "$1000{currency}") ;
  }

  @Test
  public void tesṭ̣̣̣̣VNDTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNDTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "1000 đồng 1000 (vnd) 1000(vnd)", 
        "1000{currency}", "đồng", "1000{currency}", "(", "vnd", ")", "1000{currency}", "(", "vnd", ")") ;
    verifier.verify(
        "100 ngàn 100 ngan", 
        "100{currency}", "ngàn", "100{currency}", "ngan") ;
    verifier.verify(
        "1.2 triệu 1.2triệu", 
        "1.2{currency}", "triệu", "1.2triệu{currency}") ;
    // TODO: Connector word between currency value.
    //		verifier.verify(
    //		  "3 hoặc 4 triệu",
    //		  "3{currency}", "hoặc", "4{currency}", "triệu");
  }

  @Test
  public void tesṭ̣̣̣̣VNMobileTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNMobileTokenAnalyzer()
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "0988922860 098.892.2860 0988.922860 098-892-2860 098 892 2860 (098) 892 2860", 
        "0988922860{phone}", "098.892.2860{phone}", "0988.922860{phone}", "098-892-2860{phone}", "098 892 2860{phone}", "( 098 ) 892 2860{phone}") ;
  }

  @Test
  public void tesṭ̣̣̣̣VNPhoneTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = { new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNPhoneTokenAnalyzer() } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify("36634567 3663.4567", "36634567{phone}", "3663.4567{phone}") ;
    verifier.verify(
        "(04)36634567 (04) 36634567 (04) 3663.4567 (04) 3663-4567", 
        "( 04 ) 36634567{phone}", "( 04 ) 36634567{phone}", "( 04 ) 3663.4567{phone}", "( 04 ) 3663-4567{phone}") ;
    verifier.verify("04-3663-4567 04 3663-4567", "04-3663-4567{phone}", "04 3663-4567{phone}") ;
  }

  @Test
  public void tesṭ̣̣̣̣VNNameTokenAnalyzer() throws Exception {
    TokenAnalyzer[] analyzer = { new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(), new VNNameTokenAnalyzer() } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify("Nguyễn Tấn Dũng space Lê Lai", "Nguyễn Tấn Dũng{vnname}", "space", "Lê Lai") ;
  }
}