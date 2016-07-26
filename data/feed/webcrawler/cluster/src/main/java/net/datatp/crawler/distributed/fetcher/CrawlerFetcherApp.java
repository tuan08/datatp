package net.datatp.crawler.distributed.fetcher;

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
import net.datatp.crawler.distributed.CrawlerApp;
import net.datatp.crawler.distributed.processor.FetchDataProcessorImpl;
import net.datatp.crawler.distributed.processor.WCURLExtractor;
import net.datatp.crawler.distributed.registry.CrawlerRegistry;
import net.datatp.crawler.distributed.registry.event.CrawlerEventContext;
import net.datatp.crawler.distributed.urldb.URLDatumRecordFactory;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.urldb.URLDatumFactory;
import net.datatp.jms.channel.JMSChannelGateway;
import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.text.StringUtil;
import net.datatp.zk.registry.RegistryClient;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 */
@SpringBootApplication
@ComponentScan(basePackages = { "net.datatp.crawler.distributed.fetcher", "net.datatp.crawler.distributed.site" })
@PropertySources(value = {
    @PropertySource("classpath:crawler-config.properties")
  }
)
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConfigurationProperties
public class CrawlerFetcherApp extends CrawlerApp {
  @Value("${crawler.jms.delivery.persistent}")
  private boolean jmsDeleiveryPersistent = true;
  
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
  
  @Bean(name = "URLDatumFactory")
  public URLDatumFactory createURLDatumFactory() { return new URLDatumRecordFactory(); }
  
  @Bean(name="URLFetchCommitQueue")
  public Destination createURLFetchCommitQueue(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.url.fetch.commit");
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
  
  @Bean(name = "SiteSessionManager")
  public SiteSessionManager createSiteSessionManager() { return new SiteSessionManager(); }
  
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
  
  @Bean(name = "CrawlerRegistry")
  public CrawlerRegistry createWebCrawlerRegistry(ApplicationContext appContext, 
                                                     RegistryClient registryClient) throws Exception {
    CrawlerEventContext context = new CrawlerEventContext(appContext);
    CrawlerRegistry wReg = 
        new CrawlerRegistry(registryClient).
        listenToSiteConfigEvent(context).
        listenToFetcherEvent(context);
    return wReg;
  }
  
  @Bean(name="XhtmlDocumentQueue")
  public Destination createXhtmlDocumentQueue(ConnectionFactory jmsCF) throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "crawler.output");
  }
  
  @Bean(name="XhtmlDocumentGateway")
  public JMSChannelGateway createXhtmlDocumentGateway(ConnectionFactory jmsCF,
                                                      @Qualifier("XhtmlDocumentQueue") Destination queue) {
    JMSChannelGateway gw = new JMSChannelGateway();
    gw.setDestination(queue);
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(queue);
    template.setDeliveryPersistent(jmsDeleiveryPersistent);
    gw.setJmsTemplate(template);
    return gw;
  }
  
  @Bean(name = "URLExtractor")
  public URLExtractor createURLExtractor() { return  new WCURLExtractor();  }
  
  @Bean(name = "FetchDataProcessor")
  public FetchDataProcessor createFetchDataProcessor(ApplicationContext context) {
    FetchDataProcessor fetchDataProcessor = context.getAutowireCapableBeanFactory().createBean(FetchDataProcessorImpl.class);
    return fetchDataProcessor;
  }
  
  //----------------------------------------------------------------------------------------------
  
  static public ApplicationContext run(String[] args) throws Exception {
    String[] config = { 
    };
    return run(config, args);
  }

  static public ApplicationContext run(String[] config, String[] args) throws Exception {
    String[] defaultArgs = {
      "--server.port=-1",
        
      "--spring.jmx.enabled=true",
      "--spring.jmx.default-domain=net.datatp.crawler.distributed.fetcher",
      "--spring.cloud.zookeeper.connectString=localhost:2181"
    };
    return SpringAppLauncher.launch(CrawlerFetcherApp.class, config, StringUtil.merge(defaultArgs, args));
  }

  
  static public void main(String[] args) throws Exception {
    run(args) ;
    Thread.currentThread().join() ;
  }
}