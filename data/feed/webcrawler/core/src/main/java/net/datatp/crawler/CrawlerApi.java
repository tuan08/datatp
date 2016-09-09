package net.datatp.crawler;

import java.util.List;

import net.datatp.crawler.fetcher.FetcherReport;
import net.datatp.crawler.fetcher.FetcherStatus;
import net.datatp.crawler.scheduler.URLSchedulerStatus;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteStatistic;

public interface CrawlerApi {
  public void siteCreateGroup(String group) throws Exception ;
  
  public void siteAdd(SiteConfig  ...  configs) throws Exception ;
  public void siteSave(SiteConfig ... configs) throws Exception ;
  public String[] siteRemove(String group, String ... site) throws Exception ;
  
  public List<SiteConfig> siteGetSiteConfigs() throws Exception ;
  
  public void siteReload() throws Exception ;
  
  public List<SiteStatistic> siteGetSiteStatistics() throws Exception;
  
  public URLSchedulerStatus schedulerGetURLSchedulerStatus() throws Exception;
  
  public List<URLCommitMetric> schedulerGetURLCommitReport(int max) throws Exception ;
  
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(int max) throws Exception ;
  
  public void schedulerStart() throws Exception ;
  
  public void schedulerStop() throws Exception ;

  public FetcherStatus[] getFetcherStatus() throws Exception ;
  
  public FetcherReport getFetcherReport(String id) throws Exception ;
  
  public void fetcherStart() throws Exception ;
  
  public void fetcherStop() throws Exception ;

  public CrawlerStatus getCrawlerStatus() throws Exception;
  
  public void crawlerStart() throws Exception ;
  
  public void crawlerStop() throws Exception ;
  
}
