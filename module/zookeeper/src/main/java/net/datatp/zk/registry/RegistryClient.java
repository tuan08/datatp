package net.datatp.zk.registry;

import java.io.Serializable;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;

public class RegistryClient {
  private CuratorFramework curatorClient;
  private PayloadConverter payloadConverter = new PayloadConverter.JacksonPayloadConverter();
  
  public RegistryClient(String connectString) {
    curatorClient = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
  }
  
  public void create(String path, byte[] payload) throws Exception {
    curatorClient.create().forPath(path, payload);
  }
  
  public <T extends Serializable> void create(String path, T model) throws Exception {
    byte[] payload = payloadConverter.toBytes(model);
    curatorClient.create().forPath(path, payload);
  }
  
  public void setData(String path, byte[] payload) throws Exception {
    curatorClient.setData().forPath(path, payload);
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

  public List<String> watchedGetChildren(String path) throws Exception {
    /**
     * Get children and set a watcher on the node. The watcher notification will
     * come through the CuratorListener (see setDataAsync() above).
     */
    return curatorClient.getChildren().watched().forPath(path);
  }

  public static List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher) throws Exception {
    /**
     * Get children and set the given watcher on the node.
     */
    return client.getChildren().usingWatcher(watcher).forPath(path);
  }
  
  public CuratorTransaction startTransaction(CuratorFramework client) {
    return client.inTransaction();
  }
}
