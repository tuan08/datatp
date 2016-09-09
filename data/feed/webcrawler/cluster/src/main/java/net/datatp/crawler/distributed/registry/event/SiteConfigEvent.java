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
    private boolean  add = true;
    private String[] relativePath;
    
    public Reload() {}
    
    public Reload(boolean add, String ... relativePath) {
      this.add          = add;
      this.relativePath = relativePath;
    }
    
    
    public String[] getRelativePath() { return relativePath; }
    public void setRelativePath(String[] relativePath) { this.relativePath = relativePath; }

    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      CrawlerRegistry wcReg = context.getApplicationContext().getBean(CrawlerRegistry.class);
      List<SiteConfig> siteConfigs = null;
      if(relativePath == null) {
        siteConfigs = wcReg.getSiteConfigRegistry().getAll();
      } else {
        siteConfigs = wcReg.getSiteConfigRegistry().getByRelativePaths(relativePath);
      }
      SiteContextManager siteContextManager = context.getApplicationContext().getBean(SiteContextManager.class);
      if(add) {
        siteContextManager.add(siteConfigs);
      } else {
        siteContextManager.save(siteConfigs);
      }
    }
  }
}
