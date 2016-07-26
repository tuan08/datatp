package net.datatp.crawler.distributed;

import org.springframework.context.ApplicationContext;

public class CrawlerApp {
  static public String SERIALIZABLE_PACKAGES = 
      "net.datatp.crawler.distributed.urldb,net.datatp.crawler.distributed.fetcher,java.util," + 
      "net.datatp.http,net.datatp.crawler,net.datatp.xhtml";
      
  static {
    System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES",SERIALIZABLE_PACKAGES);
  }
    
  static protected ApplicationContext appContext;
    
  static public ApplicationContext getApplicationContext() { return appContext; }
}
