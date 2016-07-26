package net.datatp.nlp.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.datatp.nlp.query.match.MatcherResourceFactory;


/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueryTemplate {
  private String query ;
  private Map<String, String> params = new HashMap<String, String>() ;

  public QueryTemplate(String query) {
    this.query = query ;
  }

  public void clearParams() { params.clear() ; }

  public void setParam(String name, String value) {
    params.put(name, value) ;
  }

  public String getQuery() { return this.query ; }

  public Query getCompileQuery(MatcherResourceFactory umFactory) throws Exception {
    String query = new String(this.query) ;
    Iterator<Map.Entry<String, String>> i = params.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, String> entry = i.next() ;
      query = query.replace("$" + entry.getKey(), entry.getValue()) ;
    }
    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    Query compileQuery = mapper.readValue(query , Query.class);
    compileQuery.compile(umFactory) ;
    return compileQuery ;
  }
}