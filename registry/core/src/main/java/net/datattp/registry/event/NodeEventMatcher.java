package net.datattp.registry.event;

import net.datattp.registry.Node;


public interface NodeEventMatcher {
  public boolean matches(Node node, NodeEvent event) throws Exception ;
}
