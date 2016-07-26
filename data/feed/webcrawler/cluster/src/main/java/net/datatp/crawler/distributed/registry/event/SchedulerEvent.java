package net.datatp.crawler.distributed.registry.event;

import net.datatp.crawler.distributed.master.CrawlerMaster;
import net.datatp.crawler.scheduler.URLScheduler;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;

public class SchedulerEvent {
  static public class InjectURL extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("SchedulerEvent:InjectURL: execute on " + context.getApplicationContext());
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
      System.err.println("SchedulerEvent:Start: execute on " + context.getApplicationContext());
      URLScheduler urlScheduler = context.getBean(URLScheduler.class);
      for(Option opt : options) {
        if(opt == Option.InjectURL) urlScheduler.injectURL();
      }
      urlScheduler.start();
    }
  }
  
  static public class Stop extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("SchedulerEvent:Stop: execute on " + context.getApplicationContext());
      URLScheduler urlScheduler = context.getBean(URLScheduler.class);
      urlScheduler.stop();
    }
  }
}
