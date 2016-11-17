package net.datatp.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.registry.event.NodeEvent;
import net.datatp.registry.event.NodeWatcher;

public class RegistryNodeEventListener {
  protected Logger logger = LoggerFactory.getLogger(RegistryNodeEventListener.class) ;
  
  protected Registry registry ; 
  
  public RegistryNodeEventListener(Registry registry) {
    this.registry = registry;
  }
  
  public void close() {
    registry = null ;
  }
  
  public boolean isClosed() {
    return registry == null || !registry.isConnect();
  }
  
  
  protected void watch(final String listenPath, final NodeWatcher nodeWatcher) throws RegistryException {
    if(registry.exists(listenPath)) {
      registry.watchModify(listenPath, nodeWatcher);
    } else {
      registry.watchExists(listenPath, new NodeWatcher() {
        @Override
        public void onEvent(NodeEvent event) throws Exception {
          if(event.getType() == NodeEvent.Type.CREATE) {
            registry.watchModify(listenPath, nodeWatcher);
            nodeWatcher.onEvent(event);
          }
        }
      });
    }
  }

}
