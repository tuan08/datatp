package net.datatp.registry.txevent;

import net.datatp.registry.Node;
import net.datatp.registry.NodeCreateMode;
import net.datatp.registry.Registry;
import net.datatp.registry.RegistryException;
import net.datatp.registry.Transaction;

public class TXEventBroadcaster {
  private Registry         registry;
  private String           eventPath;
  private Node             eventsNode ;
  
  public TXEventBroadcaster(Registry registry, String eventsPath) throws RegistryException {
    this(registry, eventsPath, true) ;
  }
  
  public TXEventBroadcaster(Registry registry, String eventsPath, boolean create) throws RegistryException {
    this.registry = registry;
    this.eventPath = eventsPath;
    if(create) {
      eventsNode = registry.createIfNotExist(eventsPath);
    } else {
      eventsNode = registry.get(eventsPath);
    }
  }
  
  public String getEventPath() { return this.eventPath ; }
  
  public Registry getRegistry() { return this.registry ; }
  
  public void initRegistry() throws RegistryException {
    eventsNode.createIfNotExists();
  }
  
  public void initRegistry(Transaction transaction) throws RegistryException {
    transaction.create(eventsNode, null, NodeCreateMode.PERSISTENT);
  }
  
  public TXEventNotificationWatcher broadcast(TXEvent event) throws RegistryException {
    eventsNode.createChild(event.getId(), event, NodeCreateMode.PERSISTENT);
    TXEventNotificationListener listener = new TXEventNotificationCompleteListener() ;
    TXEventNotificationWatcher watcher = new TXEventNotificationWatcher(this, event, listener);
    return watcher;
  }
  
  public TXEventNotificationWatcher broadcast(TXEvent event, TXEventNotificationListener listener) throws RegistryException {
    eventsNode.createChild(event.getId(), event, NodeCreateMode.PERSISTENT);
    TXEventNotificationWatcher watcher = new TXEventNotificationWatcher(this, event, listener);
    return watcher;
  }

  public void notify(TXEvent event, TXEventNotification notification) throws RegistryException {
    Node eventNode = eventsNode.getChild(event.getName());
    eventNode.createChild("notitification-", notification, NodeCreateMode.PERSISTENT_SEQUENTIAL);
  }
}
