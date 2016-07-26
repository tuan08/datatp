package net.datatp.crawler.distributed.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.datatp.crawler.distributed.registry.CrawlerRegistry;
import net.datatp.crawler.scheduler.SchedulerReporter;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;

@Component
public class SchedulerReporterImpl implements SchedulerReporter {
  @Autowired
  private CrawlerRegistry wcRegistry;
  
  public void report(URLScheduleMetric info) throws Exception {
    if(info == null) return;
    wcRegistry.getSchedulerRegistry().addReportURLScheduleMetric(info);
  }
  
  public void report(URLCommitMetric info) throws Exception {
    if(info == null) return;
    wcRegistry.getSchedulerRegistry().addReport(info);
  }
  
  
  public List<URLCommitMetric> getURLCommitReport(int max) throws Exception {
    return wcRegistry.getSchedulerRegistry().getURLCommitMetric(max);
  }
  
  public List<URLScheduleMetric> getURLScheduleReport(int max) throws Exception {
    return wcRegistry.getSchedulerRegistry().getURLScheduleMetric(max);
  }
}
