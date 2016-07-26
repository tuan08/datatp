package net.datatp.crawler.distributed;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.datatp.crawler.CrawlerApi;
import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.text.StringUtil;

/**
 * Author : Tuan Nguyen
 *          tuan@gmail.com
 * Apr 21, 2010  
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"net.datatp.crawler.rest"})
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConfigurationProperties
public class DistributedCrawlerApiApp {
  @Bean(name = "CrawlerApi")
  public CrawlerApi createCrawler() throws Exception { 
    DistributedCrawlerApi api = new DistributedCrawlerApi();
    return api;
  }
  
  static public ApplicationContext run(String[] args) throws Exception {
    String[] defaultArgs = {
      "--server.port=8080",
      "--spring.jmx.enabled=true",
      "--spring.jmx.default-domain=net.datatp.crawler.distributed.webui",
      "--spring.cloud.zookeeper.connectString=localhost:2181"
    };
    String[] config = {  };
    return SpringAppLauncher.launch(DistributedCrawlerApiApp.class, config, StringUtil.merge(defaultArgs, args));
  }
  
  static public void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}