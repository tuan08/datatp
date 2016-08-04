package net.datatp.nlp.dict;

import java.io.PrintStream;

import org.junit.Test;

import net.datatp.nlp.NLP;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TabularTokenPrinter;
import net.datatp.nlp.token.TextSegmenter;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.util.ConsoleUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeMatchingAnalyzerUnitTest {
  @Test
  public void test2() throws Exception {
    NLP nlp = new NLP("src/main/resources/nlp/vietnamese.nlp.yaml");
    TokenAnalyzer[] analyzer = 
        nlp.createTokenAnalyzers("common", "punc", "group-token-merger", "vnd", "usd", "word-tree-matching", "unknown-word-splitter");
    String text = 
      "Trong 2 gần đây, trên thị trường chưa từng có dự án chung cư nào có giá dưới 20 triệu đồng/m2 nằm trong vành đai " + 
      "3 khu vực lõi Hà Nội, ngay cả bên ngoài vùng ven như Hà Đông, Hoài Đức, hay khu Long Biên cũng phải trên 20 triệu đồng/m2." ;

    TextSegmenter textSegmenter = new TextSegmenter(analyzer);
    test(textSegmenter, text);
  }
  private void test(TextSegmenter textSegmenter, String text) throws Exception {
    IToken[] token = textSegmenter.segment(text) ;
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }
}
