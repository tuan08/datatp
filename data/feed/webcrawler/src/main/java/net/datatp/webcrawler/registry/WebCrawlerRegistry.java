package net.datatp.webcrawler.registry;

import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;

public class WebCrawlerRegistry {
  final static public String DEFAULT_ROOT = "web-crawler";

  private String             basePath;
  private RegistryClient     registryClient;
  private SiteConfigRegistry siteConfigRegistry;
  private MasterRegistry     masterRegistry;
  private FetcherRegistry    fetcherRegistry;

  public WebCrawlerRegistry(String connectString) throws Exception {
    this(DEFAULT_ROOT, connectString);
  }
  
  
  public WebCrawlerRegistry(String basePath, String connectString) throws Exception {
    this(basePath,new RegistryClient(connectString));
  }
  
  public WebCrawlerRegistry(String basePath, RegistryClient client) throws Exception {
    this.basePath  = basePath;
    registryClient = client.useNamespace(basePath);
    
    siteConfigRegistry = new SiteConfigRegistry(registryClient);
    
    masterRegistry     = new MasterRegistry(registryClient);
    fetcherRegistry    = new FetcherRegistry(registryClient);
  }

  public String getBasePath() { return this.basePath; }

  public SiteConfigRegistry getSiteConfigRegistry() { return this.siteConfigRegistry ; }

  public MasterRegistry getMasterRegistry() { return masterRegistry; }

  public FetcherRegistry getFetcherRegistry() { return fetcherRegistry; }
  
  public WebCrawlerRegistry listenToSiteConfigEvent(CrawlerEventContext context) throws Exception {
    siteConfigRegistry.listenToEvent(context);
    return this;
  }
  
  public WebCrawlerRegistry listenToMasterEvent(CrawlerEventContext context) throws Exception {
    masterRegistry.listenToEvent(context);
    return this;
  }
  
  public WebCrawlerRegistry listenToFetcherEvent(CrawlerEventContext context) throws Exception {
    fetcherRegistry.listenToEvent(context);
    return this;
  }
}