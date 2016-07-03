package net.datatp.jms.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    locations={
      "classpath:/META-INF/springframework/activemq-embedded-server.xml", 
      "classpath:/META-INF/springframework/jms-samples.xml"
    }
)
public class JmsMessageListenerTest {
  final Logger logger = LoggerFactory.getLogger(JmsMessageListenerTest.class);

  @Autowired
  private AtomicInteger counter = null;

  @Test
  public void testMessage() throws Exception {
    assertNotNull("Counter is null.", counter);
    int expectedCount = 10;

    logger.info("Testing...");

    // give listener a chance to process messages
    Thread.sleep(2 * 1000);
    assertEquals("Message is not '" + expectedCount + "'.", expectedCount, counter.get());
  }
}