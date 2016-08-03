package net.datatp.model.message;

import net.datatp.util.dataformat.DataSerializer;

public class Message {
  private byte[]          id;
  private byte[]          data;
  private String          type;
  private MessageTracking messageTracking;

  public Message() {}
  
  public Message(byte[] key, byte[] data) {
    this.id   = key ;
    this.data = data ;
  }
  
  public Message(String key, byte[] data) {
    this(key.getBytes(), data);
  }

  public Message(String key, Object obj) {
    this(key.getBytes(), DataSerializer.JSON.toBytes(obj));
  }
  
  public Message(String key, Object obj, String type) {
    this(key.getBytes(), DataSerializer.JSON.toBytes(obj));
    this.type = type;
  }
  
  public byte[] getId() { return id; }
  public void   setId(byte[] id) { this.id = id; }

  public byte[] getData() { return data; }
  public void   setData(byte[] data) { this.data = data; }

  public String getType() { return type; } ;
  public void setType(String type) {  this.type = type; }

  public MessageTracking getMessageTracking() { return messageTracking; }
  public void setMessageTracking(MessageTracking messageTracking) {
    this.messageTracking = messageTracking;
  }
  
  public <T> T getDataAs(Class<T> type) {
    return DataSerializer.JSON.fromBytes(data, type) ;
  }
  
  public String toString() {
    StringBuilder b  = new StringBuilder();
    b.append("Message: key = ").append(new String(id));
    return b.toString();
  }
}