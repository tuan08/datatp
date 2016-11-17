package net.datatp.registry;

import net.datatp.util.dataformat.DataSerializer;

public class JSONDataMapperCallback<T> implements DataMapperCallback<T> {
  final static public JSONDataMapperCallback<?> INSTANCE = new JSONDataMapperCallback<>();
  
  public JSONDataMapperCallback() {
  }
  
  @Override
  public T map(String path, byte[] data, Class<T> type) {
    return DataSerializer.JSON.fromBytes(data, type);
  }
}
