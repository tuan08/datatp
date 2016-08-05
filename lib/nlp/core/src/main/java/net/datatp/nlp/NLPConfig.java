package net.datatp.nlp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.datatp.nlp.dict.Meaning;
import net.datatp.nlp.query.Query;
import net.datatp.util.dataformat.DataReader;
import net.datatp.util.io.IOUtil;

public class NLPConfig {
  private MeaningConfig                synsets  = new MeaningConfig();
  private MeaningConfig                entities = new MeaningConfig();
  private MeaningConfig                lexicons = new MeaningConfig();
  private Map<String, String>          tokenAnalyzers;
  private String                       defaultTokenAnalyzers;
  private LinkedHashMap<String, Query> queries;
  
  public MeaningConfig getSynsets() { return synsets; }
  public void setSynsets(MeaningConfig synsets) { this.synsets = synsets; }

  public MeaningConfig getEntities() { return entities; }
  public void setEntities(MeaningConfig entities) { this.entities = entities; }

  public MeaningConfig getLexicons() { return lexicons; }
  public void setLexicons(MeaningConfig lexicons) { this.lexicons = lexicons; }
  
  @JsonProperty("token-analyzers")
  public Map<String, String> getTokenAnalyzers() { return tokenAnalyzers; }
  public void setTokenAnalyzers(Map<String, String> tokenAnalyzers) { this.tokenAnalyzers = tokenAnalyzers; }
  
  @JsonProperty("default-token-analyzers")
  public String getDefaultTokenAnalyzers() { return defaultTokenAnalyzers; }
  public void setDefaultTokenAnalyzers(String analyzers) { this.defaultTokenAnalyzers = analyzers;}
  
  public LinkedHashMap<String, Query> getQueries() { return queries; }
  public void setQueries(LinkedHashMap<String, Query> queries) { this.queries = queries; }

  static public class MeaningConfig {
    private String[]  imports ;
    private Meaning[] entries;
    
    public String[] getImports() { return imports; }
    public void setImports(String[] imports) { this.imports = imports;}
    
    public Meaning[] getEntries() { return entries; }
    public void setEntries(Meaning[] entries) { this.entries = entries; }
    
    public List<Meaning> allMeanings() throws Exception {
      List<Meaning> holder = new ArrayList<>();
      if(entries != null) {
        for(Meaning sel : entries) { holder.add(sel); }
      }
      
      if(imports != null) {
        for(String sel : imports) {
          System.out.println("Load resources..." + sel);
          InputStream is = IOUtil.loadRes(sel) ;
          DataReader reader = new DataReader(is) ;
          Meaning meaning = null ;
          while((meaning = reader.read(Meaning.class)) != null) {
            holder.add(meaning) ;
          }
        }
      }
      return holder;
    }
  }
}