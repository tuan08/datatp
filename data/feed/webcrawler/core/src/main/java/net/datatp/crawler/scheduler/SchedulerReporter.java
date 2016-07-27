package net.datatp.crawler.scheduler;

import java.util.List;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;

public interface SchedulerReporter {
  
  public void report(URLScheduleMetric info) throws Exception ;
  
  public void report(URLCommitMetric info) throws Exception ;
  
  public List<URLCommitMetric> getURLCommitReport(int max) throws Exception ;
  
  public List<URLScheduleMetric> getURLScheduleReport(int max) throws Exception ;
}