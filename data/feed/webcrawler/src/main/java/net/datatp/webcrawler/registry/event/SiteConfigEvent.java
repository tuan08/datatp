package net.datatp.webcrawler.registry.event;

import java.util.List;

import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.site.SiteConfig;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;

public class SiteConfigEvent {
  static public class Reload extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.err.println("SiteConfigEvent: execute on " + context.getApplicationContext());
      WebCrawlerRegistry wcReg = context.getApplicationContext().getBean(WebCrawlerRegistry.class);
      List<SiteConfig> siteConfigs = wcReg.getSiteConfigRegistry().getAll();
      SiteContextManager siteContextManager = context.getApplicationContext().getBean(SiteContextManager.class);
      siteContextManager.addConfig(siteConfigs);
    }
  }
}
