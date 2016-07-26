package net.datatp.crawler;

import java.util.List;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;

public interface CrawlerApi {
  public void siteCreateGroup(String group) throws Exception ;
  
  public void siteAdd(SiteConfig config) throws Exception ;
  
  public List<SiteConfig> siteGetSiteConfigs() throws Exception ;
  
  public void siteReload() throws Exception ;
  
  public List<URLCommitMetric> schedulerGetURLCommitReport(int max) throws Exception ;
  
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(int max) throws Exception ;
  
  
  public void schedulerStart() throws Exception ;
  
  public void schedulerStop() throws Exception ;
  
  public void fetcherStart() throws Exception ;
  
  public void fetcherStop() throws Exception ;

}
