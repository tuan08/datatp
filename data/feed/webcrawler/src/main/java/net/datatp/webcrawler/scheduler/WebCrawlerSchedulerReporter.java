package net.datatp.webcrawler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.scheduler.SchedulerReporter;
import net.datatp.http.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.http.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;

@Component
public class WebCrawlerSchedulerReporter implements SchedulerReporter {
  @Autowired
  private WebCrawlerRegistry wcRegistry;
  
  public void report(URLScheduleMetric info) throws Exception {
    if(info == null) return;
    wcRegistry.getMasterRegistry().addReportURLScheduleInfo(info);
  }
  
  public void report(URLCommitMetric info) throws Exception {
    if(info == null) return;
    wcRegistry.getMasterRegistry().addReport(info);
  }
  
}
