package net.datatp.registry.util;

import net.datatp.registry.Node;

public interface NodeDebugger {
  public void onCreate(RegistryDebugger registryDebugger, Node node) throws Exception ;
  public void onModify(RegistryDebugger registryDebugger, Node node) throws Exception ;
  public void onDelete(RegistryDebugger registryDebugger, Node node) throws Exception ;
}
