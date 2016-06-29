package net.datatp.kafka.log4j;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.datatp.kafka.log4j.KafkaAppender;
import net.datatp.kafka.tool.server.KafkaCluster;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.Log4jRecord;
import net.datatp.util.log.LoggerFactory;

public class KafkaAppenderUnitTest {
  static private KafkaCluster cluster;

  @BeforeClass
  static public void setUp() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("WARN");
    cluster = new KafkaCluster("./build/kafka", 1, 2);
    cluster.setNumOfPartition(1);
    cluster.start();
    Thread.sleep(2000);
  }

  @AfterClass
  static public void tearDown() throws Exception {
    cluster.shutdown();
  }

  
  @Test
  public void test() throws Exception {
    FileUtil.removeIfExist("build/buffer", false);
    KafkaAppender appender = new KafkaAppender() ;
    appender.init("127.0.0.1:9092", "log4j", "build/buffer/kafka/log4j");
    appender.activateOptions();
    for(int i = 0; i < 5; i++) {
      Log4jRecord record = new Log4jRecord() ;
      record.setTimestamp(new Date(System.currentTimeMillis()));
      record.setLevel("INFO");
      record.setMessage("message " + i);
      appender.append(record);
    }
    System.out.println("queue done..................");
    Thread.sleep(10000);
    appender.close();
  }
}