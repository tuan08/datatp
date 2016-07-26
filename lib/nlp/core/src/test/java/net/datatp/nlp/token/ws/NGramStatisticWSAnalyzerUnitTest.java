
package net.datatp.nlp.token.ws;

import org.junit.Test;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TextSegmenter;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.ws.NGramStatisticWSTokenAnalyzer;

public class NGramStatisticWSAnalyzerUnitTest {
  @Test
  public void matching() throws Exception {    
    String text = "Chúng tôi cần bán iphone giá 5 triệu từ ngày 1/3/2012";
    TokenAnalyzer[] Analyzers = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new NGramStatisticWSTokenAnalyzer().setDebug(true)
    };
    TextSegmenter textSegmenter = new TextSegmenter(Analyzers);
    IToken[] token = textSegmenter.segment(text);

    System.out.println("Segement: ");
    for (int i = 0; i < token.length; i++) {
      if(i > 0) System.out.append(" .. ");
      System.out.append(token[i].getOriginalForm());
    }
    System.out.println("\n\n");

    System.gc() ;
    Analyzers = null ;
    textSegmenter = null ;
  }
}
