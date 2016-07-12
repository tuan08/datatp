package net.datatp.webcrawler.registry.event;

import net.datatp.webcrawler.fetcher.CrawlerFetcher;
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
      System.err.println("MasterEvent:Start: execute on " + context.getApplicationContext());
      CrawlerFetcher fetcher = context.getBean(CrawlerFetcher.class);
      fetcher.start();
    }
  }
}
