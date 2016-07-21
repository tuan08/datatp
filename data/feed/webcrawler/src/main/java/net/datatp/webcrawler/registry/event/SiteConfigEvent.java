package net.datatp.webcrawler.registry.event;

import java.util.List;

import net.datatp.http.crawler.site.SiteConfig;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.site.WebCrawlerSiteContextManager;
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
      WebCrawlerSiteContextManager siteContextManager = context.getApplicationContext().getBean(WebCrawlerSiteContextManager.class);
      siteContextManager.addConfig(siteConfigs);
    }
  }
}
