package net.datatp.crawler.distributed;

import java.util.List;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.CrawlerStatus;
import net.datatp.crawler.distributed.registry.CrawlerRegistry;
import net.datatp.crawler.distributed.registry.event.FetcherEvent;
import net.datatp.crawler.distributed.registry.event.SchedulerEvent;
import net.datatp.crawler.distributed.registry.event.SiteConfigEvent;
import net.datatp.crawler.distributed.registry.event.SchedulerEvent.Start.Option;
import net.datatp.crawler.fetcher.FetcherReport;
import net.datatp.crawler.fetcher.FetcherStatus;
import net.datatp.crawler.scheduler.URLSchedulerStatus;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.zk.registry.RegistryClient;

public class DistributedCrawlerApi implements CrawlerApi {
  private CrawlerRegistry crawlerRegistry;
  
  public DistributedCrawlerApi() throws Exception {
    crawlerRegistry = new CrawlerRegistry(new RegistryClient("127.0.0.1:2181"));
  }
  
  public DistributedCrawlerApi(String zkConnects) throws Exception {
    crawlerRegistry = new CrawlerRegistry(new RegistryClient(zkConnects));
  }
  
  public CrawlerRegistry getCrawlerRegistry() { return this.crawlerRegistry; }
  
  public void siteCreateGroup(String group) throws Exception {
    crawlerRegistry.getSiteConfigRegistry().createGroup(group);
  }
  
  public void siteAdd(SiteConfig ... configs) throws Exception {
    String[] relativePath = new String[configs.length];
    for(int i = 0; i < configs.length; i++) {
      SiteConfig config = configs[i];
      crawlerRegistry.getSiteConfigRegistry().add(config);
      relativePath[i] = config.relativeStorePath();
    }
    crawlerRegistry.getSiteConfigRegistry().getEventBroadcaster().broadcast(new SiteConfigEvent.Reload(true, relativePath));
  }
  
  public void siteSave(SiteConfig ... configs) throws Exception {
    String[] relativePath = new String[configs.length];
    for(int i = 0; i < configs.length; i++) {
      SiteConfig config = configs[i];
      crawlerRegistry.getSiteConfigRegistry().save(config);
      relativePath[i] = config.relativeStorePath();
    }
    crawlerRegistry.getSiteConfigRegistry().getEventBroadcaster().broadcast(new SiteConfigEvent.Reload(false, relativePath));
  }
  
  @Override
  public String[] siteRemove(String group, String ... site) throws Exception {
    return crawlerRegistry.getSiteConfigRegistry().remove(group, site);
  }
  
  @Override
  public List<SiteConfig> siteGetSiteConfigs() throws Exception {
    return crawlerRegistry.getSiteConfigRegistry().getAll();
  }

  public void siteReload() throws Exception {
    crawlerRegistry.getSiteConfigRegistry().getEventBroadcaster().broadcast(new SiteConfigEvent.Reload());
  }
  
  @Override
  public URLSchedulerStatus getURLSchedulerStatus() throws Exception {
    return null;
  }
  
  @Override
  public List<URLCommitMetric> schedulerGetURLCommitReport(int max) throws Exception {
    return crawlerRegistry.getSchedulerRegistry().getURLCommitMetric(max);
  }

  @Override
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(int max) throws Exception {
    return crawlerRegistry.getSchedulerRegistry().getURLScheduleMetric(max);
  }
  
  @Override
  public void schedulerStart() throws Exception {
    crawlerRegistry.getSchedulerRegistry().getEventBroadcaster().broadcast(new SchedulerEvent.Start(Option.InjectURL));
  }
  
  @Override
  public void schedulerStop() throws Exception {
    crawlerRegistry.getSchedulerRegistry().getEventBroadcaster().broadcast(new SchedulerEvent.Stop());
  }

  @Override
  public FetcherStatus[] getFetcherStatus() throws Exception {
    return new FetcherStatus[] {  };
  }
  
  @Override
  public FetcherReport getFetcherReport(String id) throws Exception {
    return null;
  }
  
  @Override
  public void fetcherStart() throws Exception {
    crawlerRegistry.getFetcherRegistry().getEventBroadcaster().broadcast(new FetcherEvent.Start());
  }
  
  @Override
  public void fetcherStop() throws Exception {
    crawlerRegistry.getFetcherRegistry().getEventBroadcaster().broadcast(new FetcherEvent.Stop());
  }
  
  public CrawlerStatus getCrawlerStatus() throws Exception {
    CrawlerStatus status = new CrawlerStatus();
    status.setUrlSchedulerStatus(getURLSchedulerStatus());
    status.setFetcherStatus(getFetcherStatus());
    return status;
  }

  @Override
  public void crawlerStart() throws Exception {
    schedulerStart();
    fetcherStart();
  }

  @Override
  public void crawlerStop() throws Exception {
    fetcherStop();
    schedulerStop();
  }
}
