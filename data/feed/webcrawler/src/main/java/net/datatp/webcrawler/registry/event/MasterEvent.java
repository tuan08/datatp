package net.datatp.webcrawler.registry.event;

import net.datatp.webcrawler.master.CrawlerMaster;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;

public class MasterEvent {
  static public class InjectURL extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("MasterEvent:InjectURL: execute on " + context.getApplicationContext());
      CrawlerMaster master = context.getBean(CrawlerMaster.class);
      master.getURLFetchScheduler().injectURL() ;
    }
  }
  
  static public class Start extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    static public enum Option { InjectURL }
    
    private Option[] options = {};
    
    public Start() { }
    
    public Start(Option ... options) { 
      this.options = options;
    }
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("MasterEvent:Start: execute on " + context.getApplicationContext());
      CrawlerMaster master = context.getBean(CrawlerMaster.class);
      for(Option opt : options) {
        if(opt == Option.InjectURL) master.getURLFetchScheduler().injectURL();
      }
      master.start();
    }
  }
}
