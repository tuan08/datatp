package net.datatp.webcrawler.master;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.datatp.webcrawler.registry.WebCrawlerRegistry;

@Component
public class CrawlerRegistryManager implements Runnable {
  @Autowired
  private WebCrawlerRegistry wcRegistry;
  private Thread reporterThread;
  private boolean terminate = false;
  
  @PostConstruct
  public void onInit() {
    reporterThread = new Thread(this);
    reporterThread.setName(getClass().getName());
    reporterThread.start();
  }
  
  @PreDestroy
  public void onDestroy() {
    terminate = true;
    reporterThread.interrupt();
  }
  
  synchronized void waitForNotifination(long timeout) throws InterruptedException {
    wait(timeout);
  }
  
  @Override
  public void run() {
    while(!terminate) {
      try {
        wcRegistry.getMasterRegistry().cleanURLCommitInfo(250);
        wcRegistry.getMasterRegistry().cleanURLScheduleInfo(250);
        if(!terminate) this.waitForNotifination(30000);
      } catch (InterruptedException e) {
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
