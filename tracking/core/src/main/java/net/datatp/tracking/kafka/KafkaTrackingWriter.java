package net.datatp.tracking.kafka;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import net.datatp.kafka.KafkaAdminTool;
import net.datatp.kafka.KafkaTool;
import net.datatp.kafka.producer.AckKafkaWriter;
import net.datatp.tracking.TrackingMessage;
import net.datatp.tracking.TrackingRegistry;
import net.datatp.tracking.TrackingWriter;
import net.datatp.util.JSONSerializer;

public class KafkaTrackingWriter extends TrackingWriter {
  @Parameter(names = "--zk-connect", description = "The zk connect string")
  private String zkConnects = "localhost:2181";
  
  @Parameter(names = "--topic", description = "The input topic")
  private String topic = "tracking";
  
  @Parameter(names = "--num-of-partition", description = "The number of the partitions")
  private int    numOfPartition = 5;
  
  @Parameter(names = "--num-of-replication", description = "The number of the replications")
  private int    numOfReplication = 1;
  
  private AckKafkaWriter    kafkaWriter;
  
  public KafkaTrackingWriter(String[] args) throws Exception {
    new JCommander(this, args);
  }
  
  public void onInit(TrackingRegistry registry) throws Exception {
    KafkaAdminTool kafkaAdminTool = new KafkaAdminTool("KafkaAdminTool", zkConnects);
    if(!kafkaAdminTool.topicExits(topic)) {
      kafkaAdminTool.createTopic(topic, numOfReplication, numOfPartition);
    }
    KafkaTool kafkaTool = new KafkaTool("KafkaTool", zkConnects);
    kafkaWriter = new AckKafkaWriter("KafkaLogWriter", kafkaTool.getKafkaBrokerList()) ;
    kafkaTool.close();
  }
 
  public void onDestroy(TrackingRegistry registry) throws Exception{
    kafkaWriter.close();
  }
  
  @Override
  public void write(TrackingMessage message) throws Exception {
    String json = JSONSerializer.INSTANCE.toString(message);
    kafkaWriter.send(topic, json, 30 * 1000);
  }
  
  
}