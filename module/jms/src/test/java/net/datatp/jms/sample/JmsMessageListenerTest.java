package net.datatp.jms.sample;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.activemq.ActiveMQEmbeddedServer;
import net.datatp.springframework.SpringAppLauncher;

public class JmsMessageListenerTest {
  final Logger logger = LoggerFactory.getLogger(JmsMessageListenerTest.class);

  @Test
  public void testMessage() throws Exception {
    ActiveMQEmbeddedServer.run(null);
    String[] args = { "--activemq.broker.url=nio://localhost:61617" };
    SpringAppLauncher.launch(JMSSample.class, new String[] {"classpath:/META-INF/springframework/jms-samples.xml"}, args);
    // give listener a chance to process messages
    Thread.sleep(2 * 1000);
  }
}