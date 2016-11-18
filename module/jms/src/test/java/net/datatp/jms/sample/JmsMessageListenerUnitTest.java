package net.datatp.jms.sample;

import org.junit.Test;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.springframework.SpringAppLauncher;

public class JmsMessageListenerUnitTest {

  @Test
  public void testMessage() throws Exception {
    EmbeddedActiveMQServer.run(null);
    
    String[] args = {
        "--spring.cloud.zookeeper.enabled=false",
        "--server.port=-1",
        "--spring.jmx.default-domain=net.datatp.jms.sample",
        "--activemq.client.brokerUrl=nio://localhost:61617",
    };
    //String[] config = new String[] {"classpath:/META-INF/springframework/jms-samples.xml"};
    String[] config = new String[] {
      //"--spring.cloud.zookeeper.enabled=false",
      //"--server.port=-1"
    };
    SpringAppLauncher.launch(JMSSample.class, config, args);
    // give listener a chance to process messages
    Thread.sleep(5 * 1000);
  }
}