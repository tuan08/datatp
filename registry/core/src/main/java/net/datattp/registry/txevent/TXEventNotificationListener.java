package net.datattp.registry.txevent;

public interface TXEventNotificationListener {
  
  public void onNotification(TXEvent event, TXEventNotification notification) throws Exception ;
  
}
