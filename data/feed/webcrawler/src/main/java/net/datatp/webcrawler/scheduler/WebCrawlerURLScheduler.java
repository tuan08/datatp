package net.datatp.webcrawler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.scheduler.SchedulerReporter;
import net.datatp.http.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.http.crawler.scheduler.URLPreFetchScheduler;
import net.datatp.http.crawler.scheduler.URLScheduler;
/**
 * $Author: Tuan Nguyen$ 
 **/
@Component
@ManagedResource(
  objectName="net.datatp.webcrawler.fetcher:name=CrawlerURLScheduler", 
  description="This bean is responsible to schedule the urls and commit the downloaded urls"
)
public class WebCrawlerURLScheduler extends URLScheduler {
  @ManagedAttribute(
    description="The state of the bean, the possible value are: INIT, STARTING, STOPPING, SCHEDULING, COMMITING"
  )
  public String getState() { return this.state ; }

  @Autowired
  public void setURLPreFetchScheduler(URLPreFetchScheduler scheduler) {
    preFetchScheduler = scheduler;
  }

  @Autowired
  public void setURLPostFetchScheduler(URLPostFetchScheduler scheduler) {
    postFetchScheduler = scheduler;
  }
  
  @Autowired
  public void setSchedulerReporter(SchedulerReporter reporter) {
    this.reporter = reporter;
  }
}