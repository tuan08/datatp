package net.datatp.webcrawler.master;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import net.datatp.activemq.ActiveMQUtil;
import net.datatp.jms.channel.JMSChannelGateway;
import net.datatp.webcrawler.process.FetchDataProcessor;
import net.datatp.webcrawler.process.URLExtractor;
import net.datatp.webcrawler.urldb.URLDatumDB;

@Configuration
@ConfigurationProperties
public class CrawlerMasterAppConfig {
  @Value("${activemq.client.broker-url}")
  private String brokerUrl = "vm://localhost";
  
  @Value("${crawler.jms.delivery.persistent}")
  private boolean jmsDeleiveryPersistent = true;
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory() {
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
}
