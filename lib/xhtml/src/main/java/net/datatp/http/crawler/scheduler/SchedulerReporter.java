package net.datatp.http.crawler.scheduler;

import net.datatp.http.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.http.crawler.scheduler.metric.URLScheduleMetric;

public interface SchedulerReporter {
  
  public void report(URLScheduleMetric info) throws Exception ;
  
  public void report(URLCommitMetric info) throws Exception ;
  
}
