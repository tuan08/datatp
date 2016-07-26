package net.datatp.crawler.distributed.registry;

import net.datatp.crawler.distributed.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;

public class CrawlerRegistry {
  final static public String DEFAULT_ROOT = "crawler";

  private String             basePath;
  private RegistryClient     registryClient;
  private SiteConfigRegistry siteConfigRegistry;
  private SchedulerRegistry  schedulerRegistry;
  private FetcherRegistry    fetcherRegistry;

  public CrawlerRegistry(String connectString) throws Exception {
    this(DEFAULT_ROOT, connectString);
  }
  
  
  public CrawlerRegistry(String basePath, String connectString) throws Exception {
    this(basePath,new RegistryClient(connectString));
  }
  
  public CrawlerRegistry(RegistryClient client) throws Exception {
    this(DEFAULT_ROOT, client);
  }
  
  public CrawlerRegistry(String basePath, RegistryClient client) throws Exception {
    this.basePath  = basePath;
    registryClient = client.useNamespace(basePath);
    
    siteConfigRegistry = new SiteConfigRegistry(registryClient);
    
    schedulerRegistry  = new SchedulerRegistry(registryClient);
    fetcherRegistry    = new FetcherRegistry(registryClient);
  }

  public String getBasePath() { return this.basePath; }

  public SiteConfigRegistry getSiteConfigRegistry() { return this.siteConfigRegistry ; }

  public SchedulerRegistry getSchedulerRegistry() { return schedulerRegistry; }

  public FetcherRegistry getFetcherRegistry() { return fetcherRegistry; }
  
  public CrawlerRegistry listenToSiteConfigEvent(CrawlerEventContext context) throws Exception {
    siteConfigRegistry.listenToEvent(context);
    return this;
  }
  
  public CrawlerRegistry listenToMasterEvent(CrawlerEventContext context) throws Exception {
    schedulerRegistry.listenToEvent(context);
    return this;
  }
  
  public CrawlerRegistry listenToFetcherEvent(CrawlerEventContext context) throws Exception {
    fetcherRegistry.listenToEvent(context);
    return this;
  }
}