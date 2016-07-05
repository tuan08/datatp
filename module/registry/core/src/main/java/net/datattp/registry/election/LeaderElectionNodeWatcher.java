package net.datattp.registry.election;

import net.datattp.registry.Node;
import net.datattp.registry.Registry;
import net.datattp.registry.event.NodeEvent;
import net.datattp.registry.event.NodeWatcher;

abstract public class LeaderElectionNodeWatcher<T> extends NodeWatcher {
  Registry registry;
  Class<T> descriptorType;
  
  public LeaderElectionNodeWatcher(Registry registry,Class<T> descriptorType) {
    this.registry = registry ;
    this.descriptorType = descriptorType;
  }
  
  @Override
  public void onEvent(NodeEvent event) {
    try {
      String path = event.getPath();
      if(event.getType() == NodeEvent.Type.MODIFY) {
        Node node = registry.getRef(path);
        T data = node.getDataAs(descriptorType) ;
        onElected(event, data);
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  abstract public void onElected(NodeEvent event, T data) ;
}