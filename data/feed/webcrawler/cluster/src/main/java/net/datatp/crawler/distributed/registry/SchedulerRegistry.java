package net.datatp.crawler.distributed.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.datatp.crawler.distributed.registry.event.CrawlerEventContext;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.EventBroadcaster;
import net.datatp.zk.registry.event.EventListener;

public class SchedulerRegistry {
  final static public String EVENTS              = "/scheduler/events";
  final static public String URL_SCHEDULE_REPORT = "/scheduler/reports/url-schedule";
  final static public String URL_COMMIT_REPORT   = "/scheduler/reports/url-commit";
  
  private RegistryClient                     registryClient;
  private EventBroadcaster                   eventBroadcaster;
  private EventListener<CrawlerEventContext> eventListener;

  public SchedulerRegistry(RegistryClient client) throws Exception {
    registryClient = client.useNamespace("master");
    initRegistry();
    eventBroadcaster = new EventBroadcaster(registryClient, EVENTS);
  }
  
  public void initRegistry() throws Exception {
    registryClient.createIfNotExists(EVENTS);
    registryClient.createIfNotExists(URL_SCHEDULE_REPORT);
    registryClient.createIfNotExists(URL_COMMIT_REPORT);
  }
  
  public void onDestroy() throws IOException {
    eventListener.onDestroy();
  }
  
  public EventBroadcaster getEventBroadcaster() { return eventBroadcaster; }
  
  public void listenToEvent(CrawlerEventContext context) throws Exception {
    eventListener = new EventListener<>(context, registryClient, EVENTS);
  }
  
  public List<URLCommitMetric> getURLCommitMetric(int max) throws Exception {
    List<String> names = registryClient.getChildren(URL_COMMIT_REPORT);
    Collections.sort(names, Collections.reverseOrder());
    if(names.size() > max) names = names.subList(0, max);
    return registryClient.getChildrenAs(URL_COMMIT_REPORT, names, URLCommitMetric.class);
  }
  
  public void cleanURLCommitMetric(int keepMax) throws Exception {
    List<String> names = registryClient.getChildren(URL_COMMIT_REPORT);
    if(names.size() <= keepMax) return;
    Collections.sort(names, Collections.reverseOrder());
    names = names.subList(keepMax, names.size());
    registryClient.deleteChildren(URL_COMMIT_REPORT, names);
  }
  
  public void addReport(URLCommitMetric metric) throws Exception {
    registryClient.create(URL_COMMIT_REPORT + "/" + metric.getTime(), metric);
  }
  
  public List<URLScheduleMetric> getURLScheduleMetric(int max) throws Exception {
    List<String> names = registryClient.getChildren(URL_SCHEDULE_REPORT);
    Collections.sort(names, Collections.reverseOrder());
    if(names.size() > max) names = names.subList(0, max);
    return registryClient.getChildrenAs(URL_SCHEDULE_REPORT, names, URLScheduleMetric.class);
  }
  
  public void cleanURLScheduleMetric(int keepMax) throws Exception {
    List<String> names = registryClient.getChildren(URL_SCHEDULE_REPORT);
    if(names.size() <= keepMax) return;
    Collections.sort(names, Collections.reverseOrder());
    names = names.subList(keepMax, names.size());
    registryClient.deleteChildren(URL_COMMIT_REPORT, names);
  }
  
  public void addReportURLScheduleMetric(URLScheduleMetric metric) throws Exception {
    registryClient.create(URL_SCHEDULE_REPORT + "/" + metric.getTime(), metric);
  }
}
