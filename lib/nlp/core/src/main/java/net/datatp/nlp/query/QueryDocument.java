package net.datatp.nlp.query;

import java.util.HashMap;
import java.util.Map;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.WordTokenizer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;


public class QueryDocument {
  private TokenAnalyzer[]              analyzer;
  private Map<String, TokenCollection> fields;

  public QueryDocument() {
    fields = new HashMap<String, TokenCollection>() ;
  }
  
  public QueryDocument(TokenAnalyzer[] analyzer) {
    this.analyzer = analyzer;
    this.fields = new HashMap<String, TokenCollection>() ;
  }

  public void add(String name, String data) throws TokenException {
    IToken[] tokens = new WordTokenizer(data).allTokens() ;
    if(analyzer == null) {
      add(name, tokens, PunctuationTokenAnalyzer.INSTANCE);
    } else {
      add(name, tokens, analyzer);
    }
  }
  
  public void add(String name, IToken[] tokens) throws TokenException {
    add(name, tokens, analyzer);
  }
  
  public void add(String name, String data, TokenAnalyzer ... analyzer) throws TokenException {
    IToken[] tokens = new WordTokenizer(data).allTokens() ;
    add(name, tokens, analyzer);
  }

  public void add(String name, IToken[] token, TokenAnalyzer ... analyzer) throws TokenException {
    TokenCollection collection = new TokenCollection(token);
    collection.analyze(analyzer);
    fields.put(name, collection) ;
  }

  public TokenCollection getDocumentField(String name) { return fields.get(name) ; }

  public Map<String, TokenCollection> getDocumentFields() { return this.fields ; }
}