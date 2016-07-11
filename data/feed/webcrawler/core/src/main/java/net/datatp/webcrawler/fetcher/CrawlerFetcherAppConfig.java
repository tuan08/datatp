package net.datatp.webcrawler.fetcher;

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
import net.datatp.webcrawler.fetcher.http.HttpFetcherManager;

@Configuration
@ConfigurationProperties
public class CrawlerFetcherAppConfig {
  @Value("${activemq.client.broker-url}")
  private String brokerUrl = "vm://localhost";
  
  @Bean(name = "JMSListenerContainerFactory")
  public JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory jmsCF) {
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(jmsCF);
    return factory;
  }
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory() {
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
}
