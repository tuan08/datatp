package net.datatp.crawler.scheduler;

import java.util.List;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteStatistic;

public interface URLSchedulerReporter {
  public void setStatus(URLSchedulerStatus status) throws Exception;
  
  public URLSchedulerStatus getStatus() throws Exception;
  
  public void report(URLScheduleMetric info) throws Exception ;
  
  public void report(URLCommitMetric info) throws Exception ;
  
  public void report(List<SiteStatistic> list) throws Exception;
  
  public List<URLCommitMetric> getURLCommitReport(int max) throws Exception ;
  
  public List<URLScheduleMetric> getURLScheduleReport(int max) throws Exception ;
  
  public List<SiteStatistic> getSiteStatistics() throws Exception;
}
