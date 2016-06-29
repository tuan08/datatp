package net.datatp.flink.example.perftest;

public class Message {
  //partition or stream name or id
  private String  partition;
  private int     trackId ;
  private long    startDeliveryTime;
  private long    endDeliveryTime;
  private byte[]  key ;
  private byte[]  data;
  
  public Message() {}
  
  public Message(String  partition, int trackId, byte[] key, byte[] data) {
    this.partition = partition;
    this.trackId = trackId;
    this.key     = key ;
    this.data    = data ;
  }
  
  public Message(String partition, int trackId, String key, byte[] data) {
    this(partition, trackId, key.getBytes(), data);
  }

  public String getPartition() { return partition; }
  public void   setPartition(String partition) { this.partition = partition; }

  public int    getTrackId() { return trackId; }
  public void   setTrackId(int trackId) { this.trackId = trackId; }

  public long getStartDeliveryTime() { return startDeliveryTime; }
  public void setStartDeliveryTime(long startDeliveryTime) { 
    this.startDeliveryTime = startDeliveryTime; 
  }

  public long getEndDeliveryTime() { return endDeliveryTime; }
  public void setEndDeliveryTime(long endDeliveryTime) {
    this.endDeliveryTime = endDeliveryTime;
  }

  public byte[] getKey() { return key; }
  public void   setKey(byte[] key) { this.key = key; }

  public byte[] getData() { return data; }
  public void   setData(byte[] data) { this.data = data; }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("partition=").append(partition).append(", ").
      append("trackId=").append(trackId);
    return b.toString();
  }
}