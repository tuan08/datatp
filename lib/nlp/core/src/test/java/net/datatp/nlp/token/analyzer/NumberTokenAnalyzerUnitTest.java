package net.datatp.nlp.token.analyzer;

import org.junit.Test;

import net.datatp.nlp.token.WordTokenizerVerifier;


public class NumberTokenAnalyzerUnitTest {

  @Test
  public void test() throws Exception{
    TokenAnalyzer[] analyzer = { new CommonTokenAnalyzer(), new NumberTokenAnalyzer() } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify("chín", "chín{frequency}");
    verifier.verify("năm mươi", "năm mươi{frequency}");
    verifier.verify("hai mươi lăm", "hai mươi lăm{frequency}");
    verifier.verify("bảy mươi tám", "bảy mươi tám{frequency}");
    verifier.verify("một trăm hai mươi nhăm", "một trăm hai mươi nhăm{frequency}");
    verifier.verify("bốn nghìn linh ba", "bốn nghìn linh ba{frequency}");

    verifier.verify("hai tư ngày một tuần", "hai tư{frequency}", "ngày", "một{frequency}", "tuần");
    verifier.verify("một vạn sáu ngàn dặm", "một vạn sáu ngàn{frequency}", "dặm");
    verifier.verify("năm trăm chín hai nghìn", "năm trăm chín hai nghìn{frequency}");
    verifier.verify("tám mươi triệu người dân", "tám mươi triệu{frequency}", "người", "dân");
    //    verifier.verify("hai mươi năm", "hai mươi năm{frequency}");
  }
}
