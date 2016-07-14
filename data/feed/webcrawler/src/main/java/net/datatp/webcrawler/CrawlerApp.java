package net.datatp.webcrawler;

import org.springframework.context.ApplicationContext;

public class CrawlerApp {
  static public String SERIALIZABLE_PACKAGES = 
      "net.datatp.webcrawler.urldb,net.datatp.webcrawler.fetcher,java.util,net.datatp.xhtml";
      
    static {
      System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES",SERIALIZABLE_PACKAGES);
    }
    
    static protected ApplicationContext appContext;
    
    static public ApplicationContext getApplicationContext() { return appContext; }
}
