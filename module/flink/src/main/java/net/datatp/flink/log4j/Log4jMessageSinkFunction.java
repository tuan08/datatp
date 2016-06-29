package net.datatp.flink.log4j;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import net.datatp.model.message.Message;

public class Log4jMessageSinkFunction extends RichSinkFunction<Message> implements SinkFunction<Message> {
  private static final long serialVersionUID = 1L;
  
 
  public Log4jMessageSinkFunction() {
  }
  
  public Log4jMessageSinkFunction(String name, String kafkaConnect, String topic) {
  }
  
  
  @Override
  public void open(Configuration parameters) throws Exception {
  }

  @Override
  public void close() throws Exception {
  }
  
  public void invoke(Message message) throws Exception {
    System.out.println("Log4jMessageSinkFunction: invoke " + new String(message.getId()));
  }
}