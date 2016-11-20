package net.datatp.zk.registry.event;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import net.datatp.util.io.IOUtil;
import net.datatp.zk.registry.RegistryClient;

public class EventListener<C extends EventContext> {
  private String            path;
  private RegistryClient    registryClient;
  private C                 context;
  private PathChildrenCache commandNodeCache;

  
  public EventListener(C context, RegistryClient registryClient, String path) throws Exception {
    this.context        = context;
    this.registryClient = registryClient;
    this.path           = path;
    
    commandNodeCache = registryClient.getPathChildrenCache(path, true);
    commandNodeCache.getListenable().addListener(new CommandAddedListener());
    commandNodeCache.start();
  }
  
  public String getPath() { return path; }
  
  public RegistryClient getRegistryClient() { return this.registryClient; }
  
  public void onDestroy() throws IOException {
    commandNodeCache.close();
  }
  
  public class CommandAddedListener implements PathChildrenCacheListener {
    @SuppressWarnings("unchecked")
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent pathEvent) throws Exception {
      if(pathEvent.getType() == Type.CHILD_ADDED) {
        System.out.println("event path = " + pathEvent.getData().getPath());
        System.out.println("event data = " + pathEvent.getData().getData());
        byte[] data = pathEvent.getData().getData();
        Event event = (Event)IOUtil.deserialize(data);
        event.execute(registryClient, context);
      }
    }
  }
}
