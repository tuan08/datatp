package net.datatp.activemq;

import java.io.File;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.StorePendingDurableSubscriberMessageStoragePolicy;
import org.apache.activemq.broker.region.policy.VMPendingDurableSubscriberMessageStoragePolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.text.StringUtil;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySources(value = {
    @PropertySource("classpath:embedded-activemq.properties")
  }
)  
@ConditionalOnProperty(value = "activemq.embedded.enabled", matchIfMissing = false)
public class EmbeddedActiveMQServer {
  
  @Value("${activemq.embedded.name}")
  private String name      ;
  @Value("${activemq.embedded.home}")
  private String home      ;
  @Value("${activemq.embedded.dataDir}")
  private String dataDir   ;
  
  @Bean 
  public BrokerService createBrokerService() throws Exception { 
    BrokerService broker = new BrokerService();
    broker.setBrokerName(name);
    broker.setUseShutdownHook(false);
    broker.setPersistent(true);
    broker.setUseJmx(true);
    broker.getManagementContext().setJmxDomainName("org.apache.activemq");
    
    KahaDBPersistenceAdapter adaptor = new KahaDBPersistenceAdapter();
    adaptor.setDirectory(new File(dataDir));
    adaptor.setIndexDirectory(new File(dataDir));
    broker.setPersistenceAdapter(adaptor);
    
    PolicyEntry topicPolicy = new PolicyEntry();
    topicPolicy.setTopic(">");
    topicPolicy.setProducerFlowControl(true);
    topicPolicy.setMemoryLimit(25 * 1024 * 1024);
    topicPolicy.setPendingDurableSubscriberPolicy(new VMPendingDurableSubscriberMessageStoragePolicy());

    PolicyEntry queuePolicy = new PolicyEntry();
    queuePolicy.setTopic(">");
    queuePolicy.setProducerFlowControl(true);
    queuePolicy.setMemoryLimit(25 * 1024 * 1024);
    queuePolicy.setPendingDurableSubscriberPolicy(new StorePendingDurableSubscriberMessageStoragePolicy());

    PolicyMap policyMap = new PolicyMap();
    policyMap.put(new ActiveMQTopic(">"), topicPolicy);
    policyMap.put(new ActiveMQQueue(">"), queuePolicy);
    broker.setDestinationPolicy(policyMap);
    
    //Add plugin
    //broker.setPlugins(new BrokerPlugin[]{new JaasAuthenticationPlugin()});
    
    broker.addConnector("tcp://localhost:61616");
    broker.addConnector("nio://localhost:61617");
    broker.addConnector("vm://localhost");
    
    broker.getSystemUsage().getStoreUsage().setLimit(100 * 1024*1024*1024);
    broker.getSystemUsage().getMemoryUsage().setLimit(50 * 1024*1024);
    
    broker.start();
    return broker;
  }
  
  static public void setSerializablePackages(String[] packages) {
    System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", StringUtil.joinStringArray(packages, ","));
  }
  
  static public void run(String[] args) throws Exception {
    String[] defaultArgs = {
      "--spring.cloud.zookeeper.enabled=false",
      "--server.port=-1",
      "--activemq.embedded.enabled=true"
    };
    String[] config = {  };
    SpringAppLauncher.launch(EmbeddedActiveMQServer.class, config, StringUtil.merge(defaultArgs, args));
  }
  
  static public void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}