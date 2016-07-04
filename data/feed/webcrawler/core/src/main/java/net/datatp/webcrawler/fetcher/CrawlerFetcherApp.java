package net.datatp.webcrawler.fetcher;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import net.datatp.springframework.SpringAppLauncher;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 7, 2010  
 */
@SpringBootApplication
@ComponentScan({ "net.datatp.webcrawler.fetcher", "net.datatp.webcrawler.site" })
@EnableConfigurationProperties
public class CrawlerFetcherApp {
  static public void run(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[]{
        "--spring.jmx.enabled=true",
        "--activemq.broker.url=vm://localhost",
        
        "--crawler.data.dir=build/crawler/data",
        "--crawler.master.urldb.dir=${crawler.data.dir}/urldb",
        "--crawler.master.urldb.cleandb=false",
        "--crawler.scheduler.max-per-site=50",

        "--crawler.fetcher.number-of-threads=3",
        "--crawler.jms.delivery.persistent=true",

        //method "consume" to save the data and "dump" to throw away the data
        "--htmldocument.store.method=consume",

        "--document.consumer=org.headvances.crawler.integration.DocumentConsumer",
        "--document.consumer.storedir=${crawler.data.dir}/export"
      };
    }
    String[] config = { 
      "classpath:/META-INF/springframework/activemq-connection-factory.xml",
      "classpath:/META-INF/springframework/crawler-fetcher.xml"
    };
    SpringAppLauncher.launch(CrawlerFetcherApp.class, config, args);
  }

  static public void main(String[] args) throws Exception {
    run(args) ;
    Thread.currentThread().join() ;
  }
}