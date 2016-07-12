package net.datatp.zk.registry.event;

import org.apache.zookeeper.CreateMode;

import net.datatp.util.io.IOUtil;
import net.datatp.zk.registry.RegistryClient;

public class EventBroadcaster {
  private String         path;
  private RegistryClient registryClient;
  
  public EventBroadcaster(RegistryClient client, String path) {
    this.registryClient = client;
    this.path = path;
  }
  
  public void broadcast(Event event) throws Exception {
    byte[] data = IOUtil.serialize(event);
    registryClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path + "/" + event.getName() + "-", data);
  }
}
