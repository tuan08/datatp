package net.datatp.kafka.consumer;


import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import net.datatp.kafka.KafkaTool;
import net.datatp.kafka.consumer.KafkaPartitionReader;
import net.datatp.kafka.producer.DefaultKafkaWriter;
import net.datatp.kafka.tool.server.KafkaCluster;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;

public class KafkaPartitionReaderUnitTest {
  private KafkaCluster cluster;

  @Before
  public void setUp() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("WARN");
    FileUtil.removeIfExist("./build/cluster", false);
    
    cluster = new KafkaCluster("./build/cluster", 1, 1);
    cluster.setNumOfPartition(5);
    cluster.start();
    Thread.sleep(2000);
  }
  
  @After
  public void tearDown() throws Exception {
    cluster.shutdown();
  }
  
  @Test
  public void testPartitionReaderCommitAndRollback() throws Exception {
    String NAME = "test";
    DefaultKafkaWriter writer = new DefaultKafkaWriter(NAME, cluster.getKafkaConnect());
    for(int i = 0; i < 10; i++) {
      String hello = "Hello " + i;
      writer.send("hello", 0, "key-" + i, hello, 5000);
    }
    writer.close();
    
    KafkaTool kafkaClient = new KafkaTool(NAME, cluster.getZKConnect());
    TopicMetadata topicMetadata = kafkaClient.findTopicMetadata("hello");
    PartitionMetadata partitionMetadata = findPartition(topicMetadata.partitionsMetadata(), 0);
    KafkaPartitionReader partitionReader = new KafkaPartitionReader(NAME, kafkaClient, "hello", partitionMetadata);
    Assert.assertEquals(0, partitionReader.getCurrentOffset());
    partitionReader.fetch(10000, 3, 1000);
    partitionReader.commit();
    Assert.assertEquals(3, partitionReader.getCurrentOffset());
    partitionReader.fetch(10000, 3, 1000);
    partitionReader.rollback();
    Assert.assertEquals(3, partitionReader.getCurrentOffset());
    kafkaClient.close();
  }

  @Test
  public void testReader() throws Exception {
    String NAME = "test";
    DefaultKafkaWriter writer = new DefaultKafkaWriter(NAME, cluster.getKafkaConnect());
    for(int i = 0; i < 10; i++) {
      String hello = "Hello " + i;
      writer.send("hello", 0, "key-" + i, hello, 5000);
    }
    writer.close();
    
    readFromPartition(NAME, 0, 1, 1000/*maxWait*/);
    readFromPartition(NAME, 0, 2, 1000/*maxWait*/);
    readFromPartition(NAME, 0, 3, 1000/*maxWait*/);
    
    readFromPartition(NAME, 0, 10, 1000/*maxWait*/);
    
    readFromPartition(NAME, 0, 10, 5000/*maxWait*/);
  }
  
  private void readFromPartition(String consumerName, int partition, int maxRead, long maxWait) throws Exception {
    System.out.println("Read partition = " + partition + ", maxRead = " + maxRead + ", maxWait = " + maxWait);
    KafkaTool kafkaTool = new KafkaTool(consumerName, cluster.getZKConnect());
    TopicMetadata topicMetadata = kafkaTool.findTopicMetadata("hello");
    PartitionMetadata partitionMetadata = findPartition(topicMetadata.partitionsMetadata(), partition);
    KafkaPartitionReader partitionReader = 
        new KafkaPartitionReader(consumerName, kafkaTool, "hello", partitionMetadata);
    List<ConsumerRecord<String, byte[]>> records = partitionReader.fetch(10000, maxRead, maxWait);
    for(int i = 0; i < records.size(); i++) {
      ConsumerRecord<String, byte[]> record = records.get(i) ;
      byte[] bytes = record.value();
      System.out.println((i + 1) + ". " + new String(bytes));
    }
    partitionReader.commit();
    partitionReader.close();
    kafkaTool.close();
  }
  
  private PartitionMetadata findPartition(List<PartitionMetadata> list, int partition) {
    for(PartitionMetadata sel : list) {
      if(sel.partitionId() == partition) return sel;
    }
    return null;
  }  
}
