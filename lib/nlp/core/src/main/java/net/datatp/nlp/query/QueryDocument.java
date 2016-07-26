package net.datatp.nlp.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.WordTokenizer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;


public class QueryDocument {
  private Map<String, TokenCollection> fields ;

  public QueryDocument() {
    fields = new HashMap<String, TokenCollection>() ;
  }

  public void add(String name, String data) throws TokenException {
    IToken[] tokens = new WordTokenizer(data).allTokens() ;
    tokens = PunctuationTokenAnalyzer.INSTANCE.analyze(tokens) ;
    fields.put(name, new TokenCollection(tokens) ) ;
  }

  public void add(String name, String data, TokenAnalyzer[] analyzer) throws TokenException {
    IToken[] tokens = new WordTokenizer(data).allTokens() ;
    TokenCollection collection = new TokenCollection(tokens) ;
    collection.analyze(analyzer) ;
    fields.put(name, collection) ;
  }

  public void add(String name, IToken[] token) {
    fields.put(name, new TokenCollection(token)) ;
  }

  public void analyze(TokenAnalyzer[] analyzer) throws TokenException {
    Iterator<TokenCollection> itr = fields.values().iterator() ;
    while(itr.hasNext()) {
      TokenCollection collection = itr.next() ;
      collection.analyze(analyzer) ;
    }
  }

  public TokenCollection getDocumentField(String name) {
    return fields.get(name) ;
  }

  public Map<String, TokenCollection> getDocumentFields() { return this.fields ; }
}