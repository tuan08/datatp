package net.datatp.zk.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;


public class RegistryClient {
  private CuratorFramework curatorClient;
  private PayloadConverter payloadConverter = new PayloadConverter.JacksonPayloadConverter();
  
  public RegistryClient(String connectString) {
    curatorClient = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
    curatorClient.start();
  }
  
  public RegistryClient(CuratorFramework curatorClient) {
    this.curatorClient = curatorClient;
  }
  
  public RegistryClient(CuratorFramework curatorClient, PayloadConverter payloadConverter ) {
    this.curatorClient = curatorClient;
    this.payloadConverter = payloadConverter;
  }
  
  public RegistryClient useNamespace(String namespace) throws Exception {
    return new RegistryClient(curatorClient.usingNamespace(namespace), payloadConverter);
  }
  
  public PayloadConverter getPayloadConverter() { return payloadConverter; }
 
  public CreateBuilder create() { return curatorClient.create(); }
  
  public void create(String path) throws Exception {
    curatorClient.create().forPath(path);
  }
  
  public void createIfNotExists(String path) throws Exception {
    if(exists(path)) return ;
    curatorClient.checkExists().creatingParentContainersIfNeeded().forPath(path + "/anode");
  }
  
  
  public void create(String path, byte[] payload) throws Exception {
    curatorClient.create().forPath(path, payload);
  }
  
  public <T extends Serializable> void create(String path, T model) throws Exception {
    byte[] payload = payloadConverter.toBytes(model);
    curatorClient.create().forPath(path, payload);
  }
  
  public <T extends Serializable> void create(String path, T model, long ttl) throws Exception {
    byte[] payload = payloadConverter.toBytes(model);
    curatorClient.create().forPath(path, payload);
  }
  
  public void setData(String path, byte[] payload) throws Exception {
    curatorClient.setData().forPath(path, payload);
  }
  
  public <T extends Serializable> void setData(String path, T obj) throws Exception {
    curatorClient.setData().forPath(path, payloadConverter.toBytes(obj));
  }

  public void setDataAsync(String path, byte[] payload, CuratorListener listener) throws Exception {
    curatorClient.getCuratorListenable().addListener(listener);
    // set data for the given node asynchronously. The completion notification is done via the CuratorListener.
    curatorClient.setData().inBackground().forPath(path, payload);
  }
  
  public <T extends Serializable >void setDataAsync(String path, T obj, CuratorListener listener) throws Exception {
    setDataAsync(path, payloadConverter.toBytes(obj), listener);
  }

  public void setDataAsyncWithCallback(String path, byte[] payload, BackgroundCallback callback) throws Exception {
    // this is another method of getting notification of an async completion
    curatorClient.setData().inBackground(callback).forPath(path, payload);
  }
  
  public <T extends Serializable >void setDataAsyncWithCallback(String path, T obj, BackgroundCallback callback) throws Exception {
    setDataAsyncWithCallback(path, payloadConverter.toBytes(obj), callback);
  }

  public void delete(String path) throws Exception {
    // delete the given node
    curatorClient.delete().forPath(path);
  }

  public void guaranteedDelete(String path) throws Exception {
    curatorClient.delete().guaranteed().forPath(path);
  }
  
  public boolean exists(String path) throws Exception {
    return curatorClient.checkExists().forPath(path) != null;
  }
  
  public byte[] getData(String path) throws Exception {
    return curatorClient.getData().forPath(path);
  }
  
  public <T extends Serializable> T getDataAs(String path, Class<T> type) throws Exception {
    byte[] data = curatorClient.getData().forPath(path);
    if(data == null || data.length == 0) return null;
    return payloadConverter.fromBytes(data, type);
  }
  
  public <T extends Serializable> List<T> getChildrenAs(String path, Class<T> type) throws Exception {
    List<T> holder = new ArrayList<>();
    List<String> names = getChildren(path);
    for(int i = 0; i < names.size(); i++) {
      byte[] data = curatorClient.getData().forPath(path + "/" + names.get(i));
      if(data == null || data.length == 0) return null;
      holder.add(payloadConverter.fromBytes(data, type));
    }
    return holder;
  }
  
  public <T extends Serializable> List<T> getChildrenAs(String path, List<String> names, Class<T> type) throws Exception {
    List<T> holder = new ArrayList<>();
    for(int i = 0; i < names.size(); i++) {
      byte[] data = curatorClient.getData().forPath(path + "/" + names.get(i));
      if(data == null || data.length == 0) return null;
      holder.add(payloadConverter.fromBytes(data, type));
    }
    return holder;
  }
  
  public List<String> getChildren(String path) throws Exception {
    return curatorClient.getChildren().forPath(path);
  }
  
  public List<String> getChildren(String path, Watcher watcher) throws Exception {
    return curatorClient.getChildren().usingWatcher(watcher).forPath(path);
  }
  
  public List<String> watchedGetChildren(String path) throws Exception {
    /**
     * Get children and set a watcher on the node. The watcher notification will
     * come through the CuratorListener (see setDataAsync() above).
     */
    return curatorClient.getChildren().watched().forPath(path);
  }

  public PathChildrenCache getPathChildrenCache(String path, boolean cachedData) {
    return new PathChildrenCache(curatorClient, path, cachedData);
  }
  
  public <T extends Serializable> void createChildren(String path, List<String> names, List<T> holder) throws Exception {
    CuratorTransaction trans = startTransaction();
    CuratorTransactionFinal transFinal = trans.check().forPath(path).and();
    for(int i = 0; i < holder.size(); i++) {
      T obj = holder.get(i);
      String childPath =  path + "/" + names.get(i);
      transFinal = transFinal.create().withMode(CreateMode.PERSISTENT).forPath(childPath, payloadConverter.toBytes(obj)).and();
    }
    transFinal.commit();
  }
  
  public void deleteChildren(String path, List<String> names) throws Exception {
    CuratorTransaction trans = startTransaction();
    CuratorTransactionFinal transFinal = trans.check().forPath(path).and();
    for(int i = 0; i < names.size(); i++) {
      String childPath = path + "/" + names.get(i);
      transFinal = transFinal.delete().forPath(childPath).and();
    }
    transFinal.commit();
  }
  
  
  public CuratorTransaction startTransaction() {
    return curatorClient.inTransaction();
  }
  
  
  public CuratorTransaction startTransaction(CuratorFramework client) {
    return client.inTransaction();
  }
  
  public String formatRegistryAsText() throws Exception {
    StringBuilder out = new StringBuilder();
    dump(out);
    return out.toString();
  }
  
  public void dump(Appendable out) throws Exception  {
    out.append("/").append("\n");
    dump(out, "/", "  ");
  }
  
  void dump(Appendable out, String path, String indentation) throws Exception  {
    List<String> childNodes = getChildren(path);
    Collections.sort(childNodes);
    for(String node : childNodes) {
      out.append(indentation).append(node).append("\n");
      String childPath = path + "/" + node;
      if("/".equals(path)) childPath = "/" + node;
      dump(out, childPath, indentation + "  ");
    }
  }
}
