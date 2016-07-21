package net.datatp.webcrawler.master;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.datatp.http.crawler.scheduler.URLScheduler;
/**
 * Author : Tuan Nguyen
 *          tuan@gmail.com
 * Apr 21, 2010  
 */
@Service("CrawlerMaster")
public class CrawlerMaster {
  private static final Logger logger = LoggerFactory.getLogger(CrawlerMaster.class);

  @Autowired
  private URLScheduler urlFetchScheduler ;
  
  private CrawlerMasterInfo crawlerMasterInfo ;
  private boolean startOnInit = false ; 

  public URLScheduler getURLFetchScheduler() { return urlFetchScheduler ; }

  public CrawlerMasterInfo getCrawlerMasterInfo()  { return crawlerMasterInfo ; }

  public void setStartOnInit(boolean b) { this.startOnInit = b ;  }

  @PostConstruct
  public void onInit() throws Exception {
    this.crawlerMasterInfo = new CrawlerMasterInfo(true) ;
    this.crawlerMasterInfo.setStartTime(System.currentTimeMillis()) ;
    if(startOnInit) start();
    logger.info("onInit(), initialize the CrawlerService environment");
  }

  @PreDestroy
  public void onDestroy() throws Exception {
    stop() ;
    logger.info("onDestroy(), destroy the CrawlerService environment");
  }

  synchronized public void start() {
    logger.info("Start the CrawlerService!!!!!!!!!!!!!!!!!!!!!!!") ;
    urlFetchScheduler.start() ;
    crawlerMasterInfo.setStatus(CrawlerMasterInfo.RUNNING_STATUS) ;
  }

  synchronized public void stop() {
    urlFetchScheduler.stop() ;
    logger.info("Stop the CrawlerService!!!!!!!!!!!!!!!!!!!!!!!") ;
  }
}