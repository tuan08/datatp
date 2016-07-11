package net.datatp.jms.sample;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.support.RegistrationPolicy;

import net.datatp.activemq.ActiveMQUtil;

@SpringBootApplication
@ComponentScan({ "net.datatp.jms.sample" })
@EnableConfigurationProperties
@EnableIntegrationMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
@Configuration
public class JMSSample {
  @Value("${activemq.client.brokerUrl}")
  private String brokerUrl = "vm://localhost";
  
  @Autowired
  private  ConnectionFactory jmsCF;

  @Bean(name="destination")
  public Destination createDestination() throws Exception {
    return ActiveMQUtil.createQueue(jmsCF, "jms.sample");
  }
  
  @Bean(name="jmsProducerTemplate")
  public JmsTemplate createProducerTemplate(@Qualifier("destination") Destination destination) {
    JmsTemplate template = new JmsTemplate(jmsCF);
    template.setDefaultDestination(destination);
    return template;
  }
  

  @Bean(name = "jmsSampleContainerFactory")
  public JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory jmsCF) {
    SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
    factory.setConnectionFactory(jmsCF);
    return factory;
  }
  
  @Bean(name="jmsCF")
  public ConnectionFactory createConnectionFactory() {
    ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
    return cf;
  }
}