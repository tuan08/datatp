package net.datatp.flink.example.perftest;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import net.datatp.kafka.consumer.KafkaMessageConsumerConnector;
import net.datatp.kafka.consumer.MessageConsumerHandler;
import net.datatp.util.dataformat.DataSerializer;

public class KafkaStreamFunction<T> extends RichSourceFunction<T> implements ResultTypeQueryable<T> {
  private static final long serialVersionUID = 1L;

  private String   name;
  private String   zkConnect;
  private String   topic;
  private Class<T> type;
  transient private KafkaMessageConsumerConnector kafkaConnector;
  
  public KafkaStreamFunction() {} 
  
  public KafkaStreamFunction(String name, String zkConnect, String topic, Class<T> type) {
    this.name      = name;
    this.zkConnect = zkConnect;
    this.topic     = topic ;
    this.type      = type ;
  }
  
  
  @Override
  public void run(final SourceContext<T> ctx) throws Exception {
    kafkaConnector = 
        new KafkaMessageConsumerConnector(name, zkConnect).
        withConsumerTimeoutMs(10000).
        connect();
   
    MessageConsumerHandler handler = new MessageConsumerHandler() {
      @Override
      public void onMessage(String topic, byte[] key, byte[] message) {
        T obj = DataSerializer.JSON.fromBytes(message, type);
        beforeCollect(obj);
        ctx.collect(obj);
      }
    };
    kafkaConnector.consume(topic, handler, 1);
    kafkaConnector.awaitTermination(3600000, TimeUnit.MILLISECONDS);
  }
  
  public void beforeCollect(T mObj) {
  }
  
  @Override
  public void cancel() {
    if(kafkaConnector != null) {
      kafkaConnector.close();
      kafkaConnector = null;
    }
  }

  @Override
  public TypeInformation<T> getProducedType() { return new GenericTypeInfo<T>(type); }
}
