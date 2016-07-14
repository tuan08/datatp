package net.datatp.webcrawler.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.zookeeper.CreateMode;

import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.webcrawler.site.SiteConfig;
import net.datatp.zk.registry.PayloadConverter;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.EventBroadcaster;
import net.datatp.zk.registry.event.EventListener;

public class SiteConfigRegistry {
  final static public String SITES  = "sites";
  final static public String GROUPS = "groups";
  final static public String EVENTS = "events";

  private RegistryClient                     registryClient;
  private EventBroadcaster                   eventBroadcaster;
  private EventListener<CrawlerEventContext> eventListener;

  public SiteConfigRegistry(RegistryClient client) throws Exception {
    registryClient = client.useNamespace(SITES);
    initRegistry();
    eventBroadcaster = new EventBroadcaster(registryClient, "/" + EVENTS);
  }
  
  public void initRegistry() throws Exception {
    registryClient.createIfNotExists("/" + GROUPS);
    registryClient.createIfNotExists("/" + EVENTS);
  }
  
  public void onDestroy() throws IOException {
    eventListener.onDestroy();
  }
  
  public EventBroadcaster getEventBroadcaster() { return eventBroadcaster; }
  
  public List<String> getGroups() throws Exception {
    return registryClient.getChildren("/" + GROUPS);
  }
  
  public List<SiteConfig> getByGroup(String name) throws Exception {
    String gpath = groupPath(name);
    List<String> names = registryClient.getChildren(gpath);
    List<SiteConfig> holder = new ArrayList<>();
    for(int i = 0; i < names.size(); i++) {
      String path = gpath + "/" + names.get(i);
      holder.add(registryClient.getDataAs(path, SiteConfig.class));
    }
    return holder;
  }
  
  public List<SiteConfig> getAll() throws Exception {
    List<String> groups = getGroups();
    List<SiteConfig> holder = new ArrayList<>();
    for(int i = 0; i < groups.size(); i++) {
      holder.addAll(getByGroup(groups.get(i)));
    }
    return holder;
  }
  
  public void createGroup(String group) throws Exception {
    registryClient.createIfNotExists(groupPath(group));
  }
  
  public void add(SiteConfig config) throws Exception {
    String path = groupPath(config.getGroup()) + "/" + config.getHostname();
    registryClient.create(path, config);
  }
  
  public void update(SiteConfig config) throws Exception {
    String path = groupPath(config.getGroup()) + "/" + config.getHostname();
    registryClient.setData(path, config);
  }
  
  public void add(String group, SiteConfig ... configs) throws Exception {
    CuratorTransaction trans = registryClient.startTransaction();
    PayloadConverter converter = registryClient.getPayloadConverter();
    String gpath = groupPath(group);
    CuratorTransactionFinal transFinal = null; 
    if(!registryClient.exists(gpath)) {
      transFinal = trans.create().withMode(CreateMode.PERSISTENT).forPath(gpath).and();
    } else {
      transFinal = trans.check().forPath(gpath).and();
    }
    for(SiteConfig config : configs) {
      String path = gpath + "/" + config.getHostname();
      transFinal = transFinal.create().withMode(CreateMode.PERSISTENT).forPath(path, converter.toBytes(config)).and();
    }
    transFinal.commit();
  }
  
  public void listenToEvent(CrawlerEventContext context) throws Exception {
    eventListener = new EventListener<>(context, registryClient, "/" + EVENTS);
  }
  
  String groupPath(String name) { return "/" + GROUPS + "/" + name; }
}
