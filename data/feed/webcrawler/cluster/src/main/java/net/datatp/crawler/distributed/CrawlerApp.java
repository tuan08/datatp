package net.datatp.crawler.distributed;

import org.springframework.context.ApplicationContext;

public class CrawlerApp {
  static public String[] SERIALIZABLE_PACKAGES = {
    "java.util",
    "net.datatp.crawler.distributed.urldb", 
    "net.datatp.crawler.distributed.fetcher", 
    "net.datatp.http", 
    "net.datatp.crawler", 
    "net.datatp.xhtml" 
  };
      
    
  static protected ApplicationContext appContext;
    
  static public ApplicationContext getApplicationContext() { return appContext; }
}
