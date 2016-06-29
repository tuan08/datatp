package net.datatp.flink.example.perftest;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import net.datatp.kafka.producer.DefaultKafkaWriter;


public class KafkaSinkFunction<IN> extends RichSinkFunction<IN> implements SinkFunction<IN> {
  private static final long serialVersionUID = 1L;
  
  private String name ;
  private String kafkaConnect ;
  private String topic ;
  transient private DefaultKafkaWriter writer;
 
  public KafkaSinkFunction() {
  }
  
  public KafkaSinkFunction(String name, String kafkaConnect, String topic) {
    this.name = name; 
    this.kafkaConnect = kafkaConnect;
    this.topic = topic ;
  }
  
  
  @Override
  public void open(Configuration parameters) throws Exception {
    writer = new DefaultKafkaWriter(name, kafkaConnect);
  }

  @Override
  public void close() throws Exception {
    if(writer != null) writer.close();
  }
  
  public void invoke(IN value) throws Exception {
    writer.send(topic, value, 5000);
  }
}