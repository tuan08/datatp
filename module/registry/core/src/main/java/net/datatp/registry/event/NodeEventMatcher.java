package net.datatp.registry.event;

import net.datatp.registry.Node;


public interface NodeEventMatcher {
  public boolean matches(Node node, NodeEvent event) throws Exception ;
}
