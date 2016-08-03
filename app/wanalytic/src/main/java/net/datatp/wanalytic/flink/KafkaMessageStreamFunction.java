package net.datatp.wanalytic.flink;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import net.datatp.kafka.consumer.KafkaMessageConsumerConnector;
import net.datatp.kafka.consumer.MessageConsumerHandler;
import net.datatp.model.message.Message;
import net.datatp.util.dataformat.DataSerializer;

public class KafkaMessageStreamFunction extends RichSourceFunction<Message> implements ResultTypeQueryable<Message> {
  private static final long serialVersionUID = 1L;

  private String name;
  private String zkConnects;
  private String topic;
  private KafkaMessageConsumerConnector connector ;
  
  public KafkaMessageStreamFunction() {} 
  
  public KafkaMessageStreamFunction(String name, String zkConnects, String topic) {
    this.name       = name;
    this.zkConnects = zkConnects;
    this.topic      = topic;
  } 
  
  @Override
  public TypeInformation<Message> getProducedType() { return new GenericTypeInfo<Message>(Message.class); }
  
  @Override
  public void run(final SourceContext<Message> ctx) throws Exception {
    connector = new KafkaMessageConsumerConnector(name, zkConnects); 
    connector.
      withConsumerTimeoutMs(60000).
      connect();
    MessageConsumerHandler handler = new MessageConsumerHandler() {
      @Override
      public void onMessage(String topic, byte[] key, byte[] message) {
        Message mesgObj =  DataSerializer.JSON.fromBytes(message, Message.class);
        ctx.collect(mesgObj);
      }
    };
    connector.consume(topic, handler, 1);
    connector.awaitTermination(300, TimeUnit.MINUTES);
  } 
  
  @Override
  public void cancel() {
    connector.close();
  }
}
