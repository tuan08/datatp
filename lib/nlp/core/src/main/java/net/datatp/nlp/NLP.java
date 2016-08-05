package net.datatp.nlp;

import java.util.LinkedHashMap;
import java.util.Map;

import net.datatp.nlp.dict.LexiconDictionary;
import net.datatp.nlp.dict.MeaningDictionary;
import net.datatp.nlp.query.Query;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzerFactory;
import net.datatp.util.dataformat.DataSerializer;
import net.datatp.util.io.IOUtil;
import net.datatp.util.text.StringUtil;

public class NLP {
  private LexiconDictionary      lexiconDictionary;
  private MeaningDictionary      synsetDictionary;
  private MeaningDictionary      entityDictionary;
  private TokenAnalyzerFactory   tokenAnalyzerFactory;
  private TokenAnalyzer[]        tokenAnalyzers;
  private MatcherResourceFactory matcherResourceFactory;
  private Map<String, Query>     queries = new LinkedHashMap<>();

  public NLP(String configRes) throws Exception {
    String yamlData = IOUtil.getFileContentAsString(configRes);
    NLPConfig nlpConfig = DataSerializer.YAML.fromString(yamlData, NLPConfig.class);
    init(nlpConfig);
  }
  
  public NLP(NLPConfig config) throws Exception {
    init(config);
  }

  void init(NLPConfig config) throws Exception {
    lexiconDictionary      = new LexiconDictionary(config.getLexicons().allMeanings());
    synsetDictionary       = new MeaningDictionary("synset", config.getSynsets().allMeanings());
    entityDictionary       = new MeaningDictionary(null, config.getEntities().allMeanings());
    tokenAnalyzerFactory   = new TokenAnalyzerFactory(config.getTokenAnalyzers());
    matcherResourceFactory = new MatcherResourceFactory(synsetDictionary, entityDictionary);
    
    if(config.getDefaultTokenAnalyzers() != null) {
      String[] tokenAnalyzerNames = StringUtil.toStringArray(config.getDefaultTokenAnalyzers(), ",");
      tokenAnalyzers = new TokenAnalyzer[tokenAnalyzerNames.length];
      for(int i = 0; i < tokenAnalyzerNames.length; i++) {
       tokenAnalyzers[i] = createTokenAnalyzer(tokenAnalyzerNames[i]); 
      }
    } else {
      tokenAnalyzers = new TokenAnalyzer[0];
    }
    if(config.getQueries() != null) {
      for(Map.Entry<String, Query> entry : config.getQueries().entrySet()) {
        Query query = entry.getValue();
        query.compile(this);
        queries.put(entry.getKey(), query);
      }
    }
  }
  
  public LexiconDictionary getLexiconDictionary() { return lexiconDictionary; }

  public MeaningDictionary getSynsetDictionary() { return synsetDictionary; }

  public MeaningDictionary getEntityDictionary() { return entityDictionary; }
  
  public MatcherResourceFactory getMatcherResourceFactory() { return matcherResourceFactory; }
  
  public TokenAnalyzer[] getDefaultTokenAnalyzers() { return tokenAnalyzers; }
  
  public TokenAnalyzer createTokenAnalyzer(String name) throws Exception {
    return tokenAnalyzerFactory.createTokenAnalyzer(name, this);
  }
  public <T extends TokenAnalyzer> T createTokenAnalyzer(Class<T> type) throws Exception {
    T instance = type.newInstance();
    instance.configure(this);
    return instance;
  }
  
  public TokenAnalyzer[] createTokenAnalyzers(Class<? extends TokenAnalyzer> ... type) throws Exception {
    TokenAnalyzer[] instances = new TokenAnalyzer[type.length];
    for(int i = 0; i < type.length; i++) {
      instances[i] = type[i].newInstance();
      instances[i].configure(this);
    }
    return instances;
  }
  
  public TokenAnalyzer[] createTokenAnalyzers(String ... name) throws Exception {
    TokenAnalyzer[] analyzers = new TokenAnalyzer[name.length];
    for(int i = 0; i < analyzers.length; i++) {
      analyzers[i] = createTokenAnalyzer(name[i]);
    }
    return analyzers;
  }
  
  public Query getQuery(String name) { return queries.get(name); }
}
