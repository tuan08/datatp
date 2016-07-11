package net.datatp.webcrawler.master;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import net.datatp.springframework.SpringAppLauncher;

/**
 * Author : Tuan Nguyen
 *          tuan@gmail.com
 * Apr 21, 2010  
 */
@SpringBootApplication
@Configuration
@PropertySources(value = {
    @PropertySource("classpath:crawler-config.properties")
  }
)
@ComponentScan(basePackages = {"net.datatp.webcrawler.master", "net.datatp.webcrawler.site"})
@EnableConfigurationProperties
@EnableAutoConfiguration
public class CrawlerMasterApp {
  static private ApplicationContext appContext;
  
  static public ApplicationContext getApplicationContext() { return appContext; }
  
  static public ApplicationContext run(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
        "--server.port=-1",
        
        "--spring.jmx.enabled=true",
        "--spring.jmx.default-domain=net.datatp.webcrawler.master"
        
//        "--crawler.scheduler.max-per-site=50",
//
//        "--crawler.fetcher.number-of-threads=3",
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
//      "classpath:/META-INF/springframework/activemq-connection-factory.xml",
//      "classpath:/META-INF/springframework/crawler-master.xml",
//      "classpath:/META-INF/springframework/crawler-integration.xml"
    };
    appContext = SpringAppLauncher.launch(CrawlerMasterApp.class, config, args);
    return appContext;
  }
  
  public static void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}