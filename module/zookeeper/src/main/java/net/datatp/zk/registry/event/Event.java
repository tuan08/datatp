package net.datatp.zk.registry.event;

import java.io.Serializable;

import net.datatp.zk.registry.RegistryClient;

abstract public class Event<C extends EventContext> implements Serializable {
  
  public String getName() { return getClass().getSimpleName(); }

  abstract public void execute(RegistryClient registryClient, C context) throws Exception ;
}