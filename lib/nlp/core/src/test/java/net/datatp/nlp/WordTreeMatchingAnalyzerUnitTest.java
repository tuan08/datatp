package net.datatp.nlp;

import java.io.PrintStream;

import org.junit.Test;

import net.datatp.nlp.dict.Dictionary;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TabularTokenPrinter;
import net.datatp.nlp.token.TextSegmenter;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.USDTokenAnalyzer;
import net.datatp.nlp.token.analyzer.UnknownWordTokenSplitter;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;
import net.datatp.nlp.ws.WordTreeMatchingAnalyzer;
import net.datatp.util.ConsoleUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeMatchingAnalyzerUnitTest {
  @Test
  public void test() throws Exception {
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] analyzer = {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new GroupTokenMergerAnalyzer(),
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(), 
        new WordTreeMatchingAnalyzer(dict),
        new UnknownWordTokenSplitter(dict)
    };
    String text = "Trong 2 gần đây, trên thị trường chưa từng có dự án chung cư nào có giá dưới 20 triệu đồng/m2 nằm trong vành đai 3 khu vực lõi Hà Nội, ngay cả bên ngoài vùng ven như Hà Đông, Hoài Đức, hay khu Long Biên cũng phải trên 20 triệu đồng/m2." ;

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
