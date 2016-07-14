package net.datatp.webcrawler.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.datatp.webcrawler.master.model.URLCommitInfo;
import net.datatp.webcrawler.master.model.URLScheduleInfo;
import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.EventBroadcaster;
import net.datatp.zk.registry.event.EventListener;

public class MasterRegistry {
  final static public String EVENTS              = "/events";
  final static public String URL_SCHEDULE_REPORT = "/reports/url-schedule";
  final static public String URL_COMMIT_REPORT   = "/reports/url-commit";
  
  private RegistryClient                     registryClient;
  private EventBroadcaster                   eventBroadcaster;
  private EventListener<CrawlerEventContext> eventListener;

  public MasterRegistry(RegistryClient client) throws Exception {
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
  
  public List<URLCommitInfo> getURLCommitInfo(int max) throws Exception {
    List<String> names = registryClient.getChildren(URL_COMMIT_REPORT);
    Collections.sort(names, Collections.reverseOrder());
    if(names.size() > max) names = names.subList(0, max);
    return registryClient.getChildrenAs(URL_COMMIT_REPORT, names, URLCommitInfo.class);
  }
  
  public void cleanURLCommitInfo(int keepMax) throws Exception {
    List<String> names = registryClient.getChildren(URL_COMMIT_REPORT);
    if(names.size() <= keepMax) return;
    Collections.sort(names, Collections.reverseOrder());
    names = names.subList(keepMax, names.size());
    registryClient.deleteChildren(URL_COMMIT_REPORT, names);
  }
  
  public void addReport(URLCommitInfo info) throws Exception {
    registryClient.create(URL_COMMIT_REPORT + "/" + info.getTime(), info);
  }
  
  public List<URLScheduleInfo> getURLScheduleInfo(int max) throws Exception {
    List<String> names = registryClient.getChildren(URL_SCHEDULE_REPORT);
    Collections.sort(names, Collections.reverseOrder());
    if(names.size() > max) names = names.subList(0, max);
    return registryClient.getChildrenAs(URL_SCHEDULE_REPORT, names, URLScheduleInfo.class);
  }
  
  public void cleanURLScheduleInfo(int keepMax) throws Exception {
    List<String> names = registryClient.getChildren(URL_SCHEDULE_REPORT);
    if(names.size() <= keepMax) return;
    Collections.sort(names, Collections.reverseOrder());
    names = names.subList(keepMax, names.size());
    registryClient.deleteChildren(URL_COMMIT_REPORT, names);
  }
  
  public void addReportURLScheduleInfo(URLScheduleInfo info) throws Exception {
    registryClient.create(URL_SCHEDULE_REPORT + "/" + info.getTime(), info);
  }
}
