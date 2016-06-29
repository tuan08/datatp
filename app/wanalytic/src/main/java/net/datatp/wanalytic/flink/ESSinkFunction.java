package net.datatp.wanalytic.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.model.message.Message;

public class ESSinkFunction extends RichSinkFunction<Message> implements SinkFunction<Message> {
  private static final long serialVersionUID = 1L;
  
  private String                 name;
  private String[]               esConnect;
  private String                 index;
  private Class<?>               mappingType;
  private ESObjectClient<Object> esObjecClient;

  public ESSinkFunction() {
  }
  
  public ESSinkFunction(String name, String[] esConnect, String index, Class<?> mappingType) throws Exception {
    this.name        = name;
    this.esConnect   = esConnect;
    this.index       = index;
    this.mappingType = mappingType;
    ESObjectClient<?> esObjecClient = new ESObjectClient<>(new ESClient(esConnect), index, mappingType) ;
    esObjecClient.createIndex();
    esObjecClient.close();
  }
  
  
  @Override
  public void open(Configuration parameters) throws Exception {
    esObjecClient = new ESObjectClient<>(new ESClient(esConnect), index, mappingType) ;
  }

  @Override
  public void close() throws Exception {
    esObjecClient.close();
  }
  
  public void invoke(Message message) throws Exception {
    Object obj = message.getDataAs(mappingType);
    String id = new String(message.getId());
    esObjecClient.put(obj, id);
  }
}