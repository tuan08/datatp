package net.datatp.nlp.query;

import org.junit.Test;

import net.datatp.nlp.NLP;

public class QueryUnitTest {
  @Test
  public void testLienhe() throws Exception {
    NLP nlp = new NLP("src/main/resources/nlp/lienhe.nlp.yaml");
    Query query=  nlp.getQuery("test");
    
    QueryDocument document = query.createQueryDocument();
    document.add("title", "this is a document title");
    document.add("body",  "liên hệ 186 trương định quận hai bà trưng hà nội việt nam");
    
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }
  
  @Test
  public void testOpinion() throws Exception {
    NLP nlp = new NLP("src/main/resources/nlp/opinion.nlp.yaml");
    Query query=  nlp.getQuery("opinion");
    QueryDocument document = query.createQueryDocument();
    document.add("title", "this is a document title");
    document.add("body", "chiếc iphone này đẹp");
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }
}