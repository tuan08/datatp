package net.datatp.vm.event;

import net.datattp.registry.event.Event;
import net.datattp.registry.event.NodeEvent;

public class VMEvent extends Event {
  static public enum VMAttr { vmdescriptor, vmstatus, heartbeat, master_leader}
  
  final static public String VM_STATUS          = "vm-status" ;
  final static public String VM_HEARTBEAT       = "vm-heartbeat" ;
  final static public String VM_MASTER_ELECTION = "vm-master-election" ;
  
  public VMEvent(String name, NodeEvent event) {
    super(name, event);
  }
  
  public void attr(VMAttr attr, Object value) {
    attr(attr.toString(), value);
  }
  
  public <T> T attr(VMAttr attr) {
    return (T) attr(attr.toString());
  }
}