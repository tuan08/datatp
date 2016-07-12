package net.datatp.webcrawler.registry;

import java.io.IOException;

import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.EventBroadcaster;
import net.datatp.zk.registry.event.EventListener;

public class FetcherRegistry {
  final static public String FETCHER = "fetcher";
  final static public String EVENTS  = "events";

  private RegistryClient                     registryClient;
  private EventBroadcaster                   eventBroadcaster;
  private EventListener<CrawlerEventContext> eventListener;

  public FetcherRegistry(RegistryClient client) throws Exception {
    registryClient = client.useNamespace(FETCHER);
    initRegistry();
    eventBroadcaster = new EventBroadcaster(registryClient, "/" + EVENTS);
  }
  
  public void initRegistry() throws Exception {
    registryClient.createIfNotExists("/" + EVENTS);
  }
  
  public void onDestroy() throws IOException {
    eventListener.onDestroy();
  }
  
  public EventBroadcaster getEventBroadcaster() { return eventBroadcaster; }

  public void listenToEvent(CrawlerEventContext context) throws Exception {
    eventListener = new EventListener<>(context, registryClient, "/" + EVENTS);
  }
}
