package net.datatp.webcrawler.master;

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
import org.springframework.context.annotation.Configuration;
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
import net.datatp.webcrawler.process.FetchDataProcessor;
import net.datatp.webcrawler.process.URLExtractor;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.webcrawler.urldb.URLDatumDB;
import net.datatp.zk.registry.RegistryClient;

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
@ConfigurationProperties
public class CrawlerMasterApp extends CrawlerApp {
  @Value("${crawler.jms.delivery.persistent}")
  private boolean jmsDeleiveryPersistent = true;
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory(@Value("${crawler.activemq.client.broker-url}") String brokerUrl) {
    return new ActiveMQConnectionFactory(brokerUrl);
  }
  
  @Bean(name="URLFetchQueue")
  public Destination createURLFetchQueue(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.url.fetch");
  }
  
  @Bean(name="URLFetchCommitQueue")
  public Destination createURLFetchCommitQueue(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.url.fetch.commit");
  }
  
  @Bean(name="XHTMLOutputQueue")
  public Destination createXHTMLOutputQueue(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.output");
  }
  
  @Bean(name="URLFetchGateway")
  public JMSChannelGateway createURLFetchGateway(ConnectionFactory jmsCF,
                                                 @Qualifier("URLFetchQueue") Destination queue) {
    JMSChannelGateway gw = new JMSChannelGateway();
    gw.setDestination(queue);
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(queue);
    template.setDeliveryPersistent(jmsDeleiveryPersistent);
    gw.setJmsTemplate(template);
    return gw;
  }
  
  @Bean(name="URLFetchCommitGateway")
  public JMSChannelGateway createURLFetchCommitGateway(ConnectionFactory jmsCF,
                                                       @Qualifier("URLFetchCommitQueue") Destination queue) {
    JMSChannelGateway gw = new JMSChannelGateway();
    gw.setDestination(queue);
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(queue);
    template.setDeliveryPersistent(jmsDeleiveryPersistent);
    gw.setJmsTemplate(template);
    return gw;
  }
  
  @Bean(name="XHTMLDataGateway")
  public JMSChannelGateway createXHTMLDataGateway(ConnectionFactory jmsCF,
                                                  @Qualifier("XHTMLOutputQueue") Destination queue) {
    JMSChannelGateway gw = new JMSChannelGateway();
    gw.setDestination(queue);
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(queue);
    template.setDeliveryPersistent(jmsDeleiveryPersistent);
    gw.setJmsTemplate(template);
    return gw;
  }
  
  @Bean(name = "JMSListenerContainerFactory")
  public JmsListenerContainerFactory<?> createJMSListenerContainerFactory(ConnectionFactory jmsCF) {
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(jmsCF);
    return factory;
  }
  
  @Bean(name = "URLExtractor")
  public URLExtractor createHTTPFetchermanager(ApplicationContext context) {
    URLExtractor urlExtractor = context.getAutowireCapableBeanFactory().createBean(URLExtractor.class);
    return urlExtractor;
  }
  
  @Bean(name = "FetchDataProcessor")
  public FetchDataProcessor createFetchDataProcessor(ApplicationContext context) {
    FetchDataProcessor fetchDataProcessor = context.getAutowireCapableBeanFactory().createBean(FetchDataProcessor.class);
    return fetchDataProcessor;
  }
  
  @Bean(name = "URLDatumDB")
  public URLDatumDB createURLDatumDB(ApplicationContext context) {
    URLDatumDB db = context.getAutowireCapableBeanFactory().createBean(URLDatumDB.class);
    return db;
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
        listenToMasterEvent(context);
    return wReg;
  }
  
  //---------------------------------------------------------------------------------
  
  
  static public ApplicationContext run(String[] args) throws Exception {
    String[] config = { 
       //"classpath:/META-INF/springframework/activemq-connection-factory.xml",
       //"classpath:/META-INF/springframework/crawler-master.xml",
       //"classpath:/META-INF/springframework/crawler-integration.xml"
    };
    return run(config, args);
  }
  
  static public ApplicationContext run(String[] config, String[] args) throws Exception {
    String[] defaultArgs = {
        "--server.port=8080",
        "--spring.jmx.enabled=true",
        "--spring.jmx.default-domain=net.datatp.webcrawler.master",
        "--spring.cloud.zookeeper.connectString=localhost:2181"
    };
    appContext = SpringAppLauncher.launch(CrawlerMasterApp.class, config, StringUtil.merge(defaultArgs, args));
    return appContext;
  }
  
  public static void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}