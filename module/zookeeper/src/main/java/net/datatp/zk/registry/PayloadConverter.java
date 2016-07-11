package net.datatp.zk.registry;

import java.io.Serializable;

import net.datatp.util.JSONSerializer;

public interface PayloadConverter {
  public <T extends Serializable> byte[] toBytes(T obj) throws Exception ;
  public <T extends Serializable> T fromBytes(byte[] data, Class<T> type) throws Exception ;

  static public class JacksonPayloadConverter implements PayloadConverter {

    @Override
    public <T extends Serializable> byte[] toBytes(T obj) throws Exception {
      return JSONSerializer.INSTANCE.toBytes(obj);
    }

    @Override
    public <T extends Serializable> T fromBytes(byte[] data, Class<T> type) throws Exception {
      return JSONSerializer.INSTANCE.fromBytes(data, type);
    }
    
  }
}
