package net.datatp.webcrawler.fetcher;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import net.datatp.activemq.ActiveMQUtil;
import net.datatp.jms.channel.JMSChannelGateway;
import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.text.StringUtil;
import net.datatp.webcrawler.CrawlerApp;
import net.datatp.webcrawler.fetcher.http.HttpFetcherManager;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 */
@SpringBootApplication
@ComponentScan(basePackages = { "net.datatp.webcrawler.fetcher", "net.datatp.webcrawler.site" })
@PropertySources(value = {
    @PropertySource("classpath:crawler-config.properties")
  }
)
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConfigurationProperties
public class CrawlerFetcherApp extends CrawlerApp {
  
  @Bean(name = "JMSListenerContainerFactory")
  public JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory jmsCF) {
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(jmsCF);
    return factory;
  }
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory(@Value("${crawler.activemq.client.broker-url}") String brokerUrl) {
    return new ActiveMQConnectionFactory(brokerUrl);
  }
  
  @Bean(name="FetchDataQueue")
  public Destination createDestination(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.fetchdata");
  }
  
  @Bean(name="FetchDataGateway")
  public JMSChannelGateway createFetchDataGateway(ConnectionFactory jmsCF,
                                                  @Qualifier("FetchDataQueue") Destination fetchDataQueue) {
    JMSChannelGateway gw = new JMSChannelGateway();
    gw.setDestination(fetchDataQueue);
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(fetchDataQueue);
    gw.setJmsTemplate(template);
    return gw;
  }
  
  @Bean(name = "HttpFetcherManager")
  public HttpFetcherManager createHTTPFetchermanager(ApplicationContext context) {
    HttpFetcherManager manager = context.getAutowireCapableBeanFactory().createBean( HttpFetcherManager.class);
    return manager;
  }
  
  @Bean(name = "RegistryClient")
  public RegistryClient createRegistryClient(@Value("${crawler.zookeeper.connect}") String zkConnects) {
    RegistryClient registryClient = new RegistryClient(zkConnects);
    return registryClient;
  }
  
  @Bean(name = "WebCrawlerRegistry")
  public WebCrawlerRegistry createWebCrawlerRegistry(ApplicationContext appContext, 
                                                     RegistryClient registryClient) throws Exception {
    CrawlerEventContext context = new CrawlerEventContext(appContext);
    WebCrawlerRegistry wReg = 
        new WebCrawlerRegistry("web-crawler", registryClient).
        listenToSiteConfigEvent(context).
        listenToFetcherEvent(context);
    return wReg;
  }
  
  //----------------------------------------------------------------------------------------------
  
  static public ApplicationContext run(String[] args) throws Exception {
    String[] config = { 
      //"classpath:/META-INF/springframework/activemq-connection-factory.xml",
      //"classpath:/META-INF/springframework/crawler-fetcher.xml"
    };
    return run(config, args);
  }

  static public ApplicationContext run(String[] config, String[] args) throws Exception {
    String[] defaultArgs = {
      "--server.port=-1",
        
      "--spring.jmx.enabled=true",
      "--spring.jmx.default-domain=net.datatp.webcrawler.fetcher",
      "--spring.cloud.zookeeper.connectString=localhost:2181"
    };
    return SpringAppLauncher.launch(CrawlerFetcherApp.class, config, StringUtil.merge(defaultArgs, args));
  }

  
  static public void main(String[] args) throws Exception {
    run(args) ;
    Thread.currentThread().join() ;
  }
}