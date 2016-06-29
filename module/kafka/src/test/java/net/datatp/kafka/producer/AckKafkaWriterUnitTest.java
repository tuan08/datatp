package net.datatp.kafka.producer;

import org.junit.Test;

import net.datatp.util.log.LoggerFactory;
/**
 * @author Tuan
 */
public class AckKafkaWriterUnitTest {
  String[] topicConfigArgs = {
      "--topic", "hello",
      "--num-partition", "3",
      "--replication", "3",

      "--send-writer-type", "ack",
      "--send-period", "0",
      "--send-message-size", "1024",
      "--send-max-per-partition", "10000",
      "--send-max-duration", "45000",

      "--producer:message.send.max.retries=5",
      "--producer:retry.backoff.ms=100",

      "--producer:queue.buffering.max.ms=1000",
      "--producer:queue.buffering.max.messages=15000",

      "--producer:topic.metadata.refresh.interval.ms=-1",
      "--producer:batch.num.messages=100",
      "--producer:acks=all",
      "--producer:compression.type=gzip",

      "--consume-max-duration", "45000", "--consume-batch-fetch", "1000"
  };

  @Test
  public void testRunner() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("WARN");
    AckKafkaWriterTestRunner runner = new AckKafkaWriterTestRunner(topicConfigArgs);
    runner.setUp();
    runner.run();
    runner.tearDown();
  }
}