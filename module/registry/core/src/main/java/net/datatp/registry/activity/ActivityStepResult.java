package net.datatp.registry.activity;

import net.datatp.util.dataformat.DataSerializer;

public class ActivityStepResult {
  private String type ;
  private byte[] data ;
  private String error ;
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public byte[] getData() { return data; }
  public void setData(byte[] data) { this.data = data; }
  
  public String getError() { return error; }
  public void setError(String error) { this.error = error; }

  public boolean hasError() { return error != null ; }
  
  public <T> T dataAs(Class<T> type) {
    return DataSerializer.JSON.fromBytes(data, type);
  }
  
  public <T> void withDataAs(T object) {
    type = object.getClass().getName();
    data = DataSerializer.JSON.toBytes(object) ;
  }
}