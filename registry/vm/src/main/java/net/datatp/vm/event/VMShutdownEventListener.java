package net.datatp.vm.event;

import net.datatp.vm.service.VMService;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryException;
import net.datattp.registry.event.NodeEvent;
import net.datattp.registry.event.NodeEventListener;

abstract public class VMShutdownEventListener extends NodeEventListener<VMEvent> {
  
  public VMShutdownEventListener(Registry registry) throws RegistryException {
    super(registry, true);
    watch(VMService.SHUTDOWN_EVENT_PATH);
  }

  @Override
  public VMEvent toAppEvent(Registry registry, NodeEvent nodeEvent) throws Exception  {
    VMEvent scribenginEvent = new VMEvent("shutdown", nodeEvent) ;
    Boolean shutdown = registry.getDataAs(VMService.SHUTDOWN_EVENT_PATH, Boolean.class);
    scribenginEvent.attr("shutdown", shutdown);
    return scribenginEvent;
  }

  @Override
  public void onEvent(VMEvent event) throws Exception {
    boolean shutdown = event.attr("shutdown");
    if(shutdown) onShutdownEvent();
  }
  
  abstract public void onShutdownEvent() throws Exception ;
}