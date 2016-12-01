package net.datatp.crawler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.basic.Crawler;
import net.datatp.crawler.basic.CrawlerConfig;
import net.datatp.crawler.distributed.urldb.URLDatumRecordDB;
import net.datatp.crawler.processor.ESXDocProcessor;
import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.io.IOUtil;
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
@Import({ SecurityConfig.class })
public class BasicCrawlerApp {
  @Value("${crawler.site.config.file:#{null}}")
  private String siteConfigFile;
  
  @Value("${crawler.xdoc.processor:es}")
  private String xdocProcessor = null;
  
  @Value("${crawler.url.recorddb.dir}")
  private String urlRecordDBDir = null;
  
  @Value("${crawler.es.address:127.0.0.1:9300}")
  private String esConnects = null;
  
  @Bean(name = "CrawlerApi")
  public CrawlerApi createCrawler() throws Exception { 
    Crawler crawler = new Crawler();
    
    if(urlRecordDBDir != null) {
      URLDatumRecordDB urlDB = new URLDatumRecordDB(urlRecordDBDir, false);
      crawler.configure(new CrawlerConfig(), urlDB);
    } else {
      crawler.configure(new CrawlerConfig());
    }
    
    if(siteConfigFile != null) {
      byte[] jsonData = IOUtil.getFileContentAsBytes(siteConfigFile);
      CrawlerApi.importJson(crawler, jsonData);
    }
    
    System.out.println("BasicCrawlerApp: xdocProcessor = " + xdocProcessor + ", esConnects = " + esConnects);
    if("es".equals(xdocProcessor)) {
      String[] esConnect = StringUtil.splitAsArray(esConnects, ',');
      ESXDocProcessor xdocProcessor = new ESXDocProcessor("xdoc", esConnect);
      crawler.setXDocProcessor(xdocProcessor);
    }
    return crawler;
  }
  
  static public ApplicationContext run(String[] args) throws Exception {
    System.out.println("run()");
    if(args == null || args.length == 0) {
      args  = new String[] {
          "--spring.cloud.zookeeper.enabled=false",
          "--spring.http.multipart.enabled=true",
          "--server.port=8080",
      };
    }
    System.out.println("Launch BasicCrawlerApp with args: " + StringUtil.joinStringArray(args, " "));
    String[] config = {  };
    return SpringAppLauncher.launch(BasicCrawlerApp.class, config, args);
  }

  public static void main(String[] args) throws Exception {
    System.out.println("main()");
    run(args);
    Thread.currentThread().join();
  }
}