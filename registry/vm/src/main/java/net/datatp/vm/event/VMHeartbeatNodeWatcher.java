package net.datatp.vm.event;

import net.datatp.vm.VMDescriptor;
import net.datattp.registry.Registry;
import net.datattp.registry.event.NodeEvent;
import net.datattp.registry.event.NodeWatcher;

public class VMHeartbeatNodeWatcher extends NodeWatcher {
  private Registry registry;
  
  public VMHeartbeatNodeWatcher(Registry registry) {
    this.registry = registry;
  }
  
  @Override
  public void onEvent(NodeEvent event) {
    if(!registry.isConnect()) return;
    try {
      String path = event.getPath();
      String descriptorPath = path.replace("/status/heartbeat", "") ;
      if(event.getType() == NodeEvent.Type.CREATE) {
        VMDescriptor vmDescriptor = registry.getDataAs(descriptorPath, VMDescriptor.class);
        onConnected(event, vmDescriptor);
      } else if(event.getType() == NodeEvent.Type.DELETE) {
        VMDescriptor vmDescriptor = registry.getDataAs(descriptorPath, VMDescriptor.class);
        onDisconnected(event, vmDescriptor);
        //setComplete();
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public void onConnected(NodeEvent event, VMDescriptor vmDescriptor) {
  }
  
  public void onDisconnected(NodeEvent event, VMDescriptor vmDescriptor) {
  }
}