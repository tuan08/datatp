package net.datatp.webcrawler.registry.event;

import org.springframework.context.ApplicationContext;

import net.datatp.zk.registry.event.EventContext;

public class CrawlerEventContext extends EventContext {
  private ApplicationContext appContext;
  
  public CrawlerEventContext(ApplicationContext appContext) {
    this.appContext = appContext;
  }
  
  public ApplicationContext getApplicationContext() { return appContext; }
  
  public <T> T getBean(Class<T> type) { return appContext.getBean(type) ; }
}
