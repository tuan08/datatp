package net.datatp.nlp.query;

import java.util.LinkedHashMap;
import java.util.List;

import net.datatp.nlp.dict.Meaning;

public class QueryProject {
  private List<Meaning>                synsets;
  private List<Meaning>                entities  ;
  private LinkedHashMap<String, Query> queries;
  
  public List<Meaning> getSynsets() { return synsets; }
  public void setSynsets(List<Meaning> synsets) { this.synsets = synsets; }
  
  public List<Meaning> getEntities() { return entities; }
  public void setEntities(List<Meaning> entities) { this.entities = entities; }
  
  public LinkedHashMap<String, Query> getQueries() { return queries; }
  public void setQueries(LinkedHashMap<String, Query> queries) { this.queries = queries; }
}
