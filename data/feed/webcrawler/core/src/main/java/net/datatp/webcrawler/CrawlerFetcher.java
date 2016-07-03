package net.datatp.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import net.datatp.webcrawler.fetch.http.HttpFetcherManager;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jul 7, 2010  
 */
public class CrawlerFetcher {
  private static ApplicationContext applicationContext ;
  private static final Logger logger = LoggerFactory.getLogger(CrawlerFetcher.class);

  @Autowired
  private HttpFetcherManager fetcherManager ;

  private boolean startOnInit = false ;

  public HttpFetcherManager getFetcherManager() { return fetcherManager ; }

  public void setStartOnInit(boolean b) { this.startOnInit = b ; }

  public void onInit() { 
    if(startOnInit) start() ;
  }

  public void start() {
    fetcherManager.start() ;
    logger.info("CrawlerWorker Start!!!!!!!!!!!!!!!!!!!!!!!!!") ;
  }

  public void stop() {
    fetcherManager.stop() ;
    logger.info("CrawlerWorker Stop!!!!!!!!!!!!!!!!!!!!!!!!!") ;
  }

  static public ApplicationContext getApplicationContext() { return applicationContext ; }
  static public void setApplicationContext(ApplicationContext context) {
    applicationContext = context ;
  }

  static public void run() throws Exception {
    final GenericApplicationContext ctx = new GenericApplicationContext() ;
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
    String[] res = {
      "classpath:/META-INF/crawler-fetcher.xml",
    } ;
    xmlReader.loadBeanDefinitions(res) ;
    ctx.refresh() ;
    ctx.registerShutdownHook() ;
    setApplicationContext(ctx) ;
  }

  static public void main(String[] args) throws Exception {
    run() ;
    Thread.currentThread().join() ;
  }
}