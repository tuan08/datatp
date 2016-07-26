package net.datatp.crawler.distributed.registry.event;

import java.util.List;

import net.datatp.crawler.distributed.registry.CrawlerRegistry;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;

public class SiteConfigEvent {
  static public class Reload extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("SiteConfigEvent: execute on " + context.getApplicationContext());
      CrawlerRegistry wcReg = context.getApplicationContext().getBean(CrawlerRegistry.class);
      List<SiteConfig> siteConfigs = wcReg.getSiteConfigRegistry().getAll();
      SiteContextManager siteContextManager = context.getApplicationContext().getBean(SiteContextManager.class);
      siteContextManager.addConfig(siteConfigs);
    }
  }
}
