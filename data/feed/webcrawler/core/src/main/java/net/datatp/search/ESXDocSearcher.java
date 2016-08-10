package net.datatp.search;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.es.ESQueryExecutor;
import net.datatp.xhtml.XDoc;

public class ESXDocSearcher {
  private String               index;
  private ESClient             esclient;
  private ESObjectClient<XDoc> esXDocClient;

  public ESXDocSearcher(String index, String[] esAddress) throws Exception {
    this.index        = index;
    this.esclient     = new ESClient(esAddress);
    this.esXDocClient = new ESObjectClient<>(esclient, index, XDoc.class) ;
  }
  
  public ESQueryExecutor getQueryExecutor() { 
    return new ESQueryExecutor(index, esclient); 
  }
  
  public ESQueryExecutor getContentQueryExecutor(String query) { 
    ESQueryExecutor executor = new ESQueryExecutor(index, esclient); 
    String[] field = {"entity.content.title^5", "entity.content.description^3", "entity.content.content" } ;
    executor.matchTerms(field, query);
    return executor;
  }
}
