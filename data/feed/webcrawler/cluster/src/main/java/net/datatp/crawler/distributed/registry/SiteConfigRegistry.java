package net.datatp.crawler.distributed.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.zookeeper.CreateMode;

import net.datatp.crawler.distributed.registry.event.CrawlerEventContext;
import net.datatp.crawler.site.SiteConfig;
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
  
  public SiteConfig getByRelativePath(String relativePath) throws Exception {
    String path = "/" + GROUPS + "/" + relativePath;
    return registryClient.getDataAs(path, SiteConfig.class);
  }

  public List<SiteConfig> getByRelativePaths(String[] relativePath) throws Exception {
    List<SiteConfig> holder = new ArrayList<>();
    for(int i = 0; i < relativePath.length; i++) {
      holder.add(getByRelativePath(relativePath[i]));
    }
    return holder;
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
    String path = storePath(config);
    registryClient.create(path, config);
  }
  
  public void save(SiteConfig config) throws Exception {
    String path = storePath(config);
    registryClient.setData(path, config);;
  }
  
  
  public void update(SiteConfig config) throws Exception {
    String path = storePath(config);
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
      String path = storePath(config);
      transFinal = transFinal.create().withMode(CreateMode.PERSISTENT).forPath(path, converter.toBytes(config)).and();
    }
    transFinal.commit();
  }
  
  public void listenToEvent(CrawlerEventContext context) throws Exception {
    eventListener = new EventListener<>(context, registryClient, "/" + EVENTS);
  }
  
  String groupPath(String name) { return "/" + GROUPS + "/" + name; }
  
  String storePath(SiteConfig config) { return "/" + GROUPS + "/" + config.relativeStorePath(); }
}
