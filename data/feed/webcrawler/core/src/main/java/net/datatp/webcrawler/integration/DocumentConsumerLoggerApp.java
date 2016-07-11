package net.datatp.webcrawler.integration;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
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
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.jms.DynamicJmsTemplate;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import net.datatp.springframework.SpringAppLauncher;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 7, 2010  
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "net.datatp.webcrawler.integration" })
@PropertySources(value = {
    @PropertySource("classpath:crawler-config.properties")
  }
)
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConfigurationProperties
public class DocumentConsumerLoggerApp {
  @Value("${activemq.client.broker-url}")
  private String brokerUrl = "vm://localhost";
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory() {
    return new ActiveMQConnectionFactory(brokerUrl);
  }
  
  @Bean(name = "DocumentConsumerLogger")
  public DocumentConsumerLogger createDocumentConsumerLogger(ApplicationContext context) {
    DocumentConsumerLogger logger = context.getAutowireCapableBeanFactory().createBean(DocumentConsumerLogger.class);
    return logger;
  }
  
  @Bean(name="CrawlerOutputChannel")
  public PollableChannel createCrawlerOutputChannel() {
    //return new DirectChannel();
    return new QueueChannel(10);
  }
  
//  @Bean(name = PollerMetadata.DEFAULT_POLLER)
//  public PollerMetadata defaultPoller() {
//    PollerMetadata pollerMetadata = new PollerMetadata();
//    pollerMetadata.setTrigger(new PeriodicTrigger(10));
//    return pollerMetadata;
//  }
//  
//  @Bean
//  @ServiceActivator(inputChannel = "CrawlerOutputChannel")
//  public MessageHandler jsmOutboundAdapter(ConnectionFactory jmsCF) {
//    JmsTemplate template = new DynamicJmsTemplate();
//    template.setConnectionFactory(jmsCF);
//    JmsSendingMessageHandler handler = new JmsSendingMessageHandler(template);
//    handler.setDestinationName("crawler.output");
//    return handler;
//  }
//  
//  @Bean
//  @ServiceActivator(inputChannel = "CrawlerOutputChannel")
//  public MessageSource<Object> jsmCrawlerOutputChannel(ConnectionFactory jmsCF) {
//    JmsTemplate template = new DynamicJmsTemplate();
//    template.setConnectionFactory(jmsCF);
//    JmsDestinationPollingSource source = new JmsDestinationPollingSource(template);
//    source.setDestinationName("crawler.output");
//    return source;
//  }
//  
  static private ApplicationContext appContext;

  static public ApplicationContext getApplicationContext() { return appContext; }

  static public ApplicationContext run(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
          "--server.port=-1",
          "--spring.jmx.enabled=true",
          "--spring.jmx.default-domain=net.datatp.webcrawler.integration"
      };
    }
    String[] config = { 
     // "classpath:/META-INF/connection-factory-activemq.xml",
      "classpath:/META-INF/springframework/crawler-integration-logger.xml"
    };
    return SpringAppLauncher.launch(DocumentConsumerLoggerApp.class, config, args);
  }

  static public void main(String[] args) throws Exception {
  }

}
