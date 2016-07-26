package net.datatp.crawler.distributed.registry.event;

import net.datatp.crawler.distributed.fetcher.CrawlerFetcher;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;

public class FetcherEvent {
  static public class Start extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    static public enum Option { }
    
    private Option[] options = {};
    
    public Start() { }
    
    public Start(Option ... options) { 
      this.options = options;
    }
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("FetcherEvent:Start: execute on " + context.getApplicationContext());
      CrawlerFetcher fetcher = context.getBean(CrawlerFetcher.class);
      fetcher.start();
    }
  }
  
  static public class Stop extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;


    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("FetcherEvent:Stopt: execute on " + context.getApplicationContext());
      CrawlerFetcher fetcher = context.getBean(CrawlerFetcher.class);
      fetcher.stop();
    }
  }
}
