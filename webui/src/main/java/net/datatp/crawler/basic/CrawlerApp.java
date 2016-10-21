package net.datatp.crawler.basic;

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
@ComponentScan(basePackages = { "net.datatp.crawler.rest" })
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConfigurationProperties
public class CrawlerApp {
  @Bean(name = "CrawlerApi")
  public CrawlerApi createCrawler() throws Exception { 
    Crawler crawler = new Crawler();
    crawler.configure(new CrawlerConfig());
    return crawler;
  }
  
  static public ApplicationContext run(String[] args) throws Exception {
    String[] defaultArgs = {
      "--spring.cloud.zookeeper.enabled=false",
      "--spring.http.multipart.enabled=true",
      "--server.port=8080",
    };
    String[] config = {  };
    return SpringAppLauncher.launch(CrawlerApp.class, config, StringUtil.merge(defaultArgs, args));
  }
  
  static public void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();
  }
}