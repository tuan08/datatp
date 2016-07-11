package net.datatp.webcrawler.fetcher;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import net.datatp.springframework.SpringAppLauncher;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 7, 2010  
 */
@SpringBootApplication
@ComponentScan(basePackages = { "net.datatp.webcrawler.fetcher", "net.datatp.webcrawler.site" })
@PropertySources(value = {
    @PropertySource("classpath:crawler-config.properties")
  }
)
@EnableConfigurationProperties
@EnableAutoConfiguration
public class CrawlerFetcherApp {
  static public String SERIALIZABLE_PACKAGES = 
    "net.datatp.webcrawler.urldb,net.datatp.webcrawler.fetcher,java.util,net.datatp.xhtml";
  
  static {
    System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES",SERIALIZABLE_PACKAGES);
  }
  
  static private ApplicationContext appContext;
  
  static public ApplicationContext getApplicationContext() { return appContext; }
  
  static public ApplicationContext run(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
        "--server.port=-1",
        
        "--spring.jmx.enabled=true",
        "--spring.jmx.default-domain=net.datatp.webcrawler.fetcher"
        
//        "--crawler.data.dir=build/crawler/data",
//        "--crawler.master.urldb.dir=${crawler.data.dir}/urldb",
//        "--crawler.master.urldb.cleandb=false",
//        "--crawler.scheduler.max-per-site=50",
//
//        "--crawler.fetcher.number-of-threads=3",
//        "--crawler.fetcher.num-of-threads=3",
//        
//        "--crawler.jms.delivery.persistent=true",
//
//        //method "consume" to save the data and "dump" to throw away the data
//        "--htmldocument.store.method=consume",
//
//        "--document.consumer=org.headvances.crawler.integration.DocumentConsumer",
//        "--document.consumer.storedir=${crawler.data.dir}/export"
      };
    }
    String[] config = { 
      //"classpath:/META-INF/springframework/activemq-connection-factory.xml",
      //"classpath:/META-INF/springframework/crawler-fetcher.xml"
    };
    return SpringAppLauncher.launch(CrawlerFetcherApp.class, config, args);
  }

  static public void main(String[] args) throws Exception {
    run(args) ;
    Thread.currentThread().join() ;
  }
}