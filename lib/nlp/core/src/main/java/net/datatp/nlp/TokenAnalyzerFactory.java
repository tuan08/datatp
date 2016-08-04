package net.datatp.nlp;

import java.util.HashMap;
import java.util.Map;

import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.DateTokenAnalyzer;
import net.datatp.nlp.token.analyzer.EmailTokenAnalyzer;
import net.datatp.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TimeTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.USDTokenAnalyzer;
import net.datatp.nlp.token.analyzer.UnknownWordTokenSplitter;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNMobileTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNPhoneTokenAnalyzer;
import net.datatp.nlp.ws.WordTreeMatchingAnalyzer;

public class TokenAnalyzerFactory {
  private Map<String, Class<? extends TokenAnalyzer >> tokenAnalyzerTypes = new HashMap<>();
  
  public TokenAnalyzerFactory() {
    add("common",   CommonTokenAnalyzer.class);
    add("punc",     PunctuationTokenAnalyzer.class);
    add("date",     DateTokenAnalyzer.class);
    add("time",     TimeTokenAnalyzer.class);
    add("email",    EmailTokenAnalyzer.class);
    add("vnd",      VNDTokenAnalyzer.class);
    add("usd",      USDTokenAnalyzer.class);
    add("vnphone",  VNPhoneTokenAnalyzer.class);
    add("vnmobile", VNMobileTokenAnalyzer.class);
    add("group-token-merger", GroupTokenMergerAnalyzer.class);
    add("word-tree-matching",    WordTreeMatchingAnalyzer.class);
    add("unknown-word-splitter", UnknownWordTokenSplitter.class);
  }
  
  public TokenAnalyzerFactory(Map<String, String> others) throws Exception {
    this();
    if(others != null) {
      for(Map.Entry<String, String> entry : others.entrySet()) {
        Class<? extends TokenAnalyzer> type = (Class<? extends TokenAnalyzer>) Class.forName(entry.getValue());
        add(entry.getKey(), type);
      }
    }
  }
  
  public void add(String name, Class<? extends TokenAnalyzer> type) {
    tokenAnalyzerTypes.put(name, type);
  }
  
  public TokenAnalyzer createTokenAnalyzer(String name, NLP nlp) throws Exception {
    Class<? extends TokenAnalyzer> type = tokenAnalyzerTypes.get(name);
    TokenAnalyzer instance = type.newInstance();
    instance.configure(nlp);
    return instance;
  }
}
