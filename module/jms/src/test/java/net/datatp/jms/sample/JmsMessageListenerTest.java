package net.datatp.jms.sample;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.springframework.SpringAppLauncher;

public class JmsMessageListenerTest {
  final Logger logger = LoggerFactory.getLogger(JmsMessageListenerTest.class);

  @Test
  public void testMessage() throws Exception {
    EmbeddedActiveMQServer.run(null);
    String[] args = {
        "spring.jmx.default-domain=net.datatp.jms.sample",
        "--activemq.client.brokerUrl=nio://localhost:61617",
    };
    //String[] config = new String[] {"classpath:/META-INF/springframework/jms-samples.xml"};
    String[] config = new String[] {};
    SpringAppLauncher.launch(JMSSample.class, config, args);
    // give listener a chance to process messages
    Thread.sleep(2 * 1000);
  }
}