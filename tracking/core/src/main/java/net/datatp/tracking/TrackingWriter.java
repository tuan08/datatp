package net.datatp.tracking;

abstract public class TrackingWriter {
  public void onInit(TrackingRegistry registry) throws Exception {
  }
 
  public void onDestroy(TrackingRegistry registry) throws Exception{
  }
  
  abstract public void write(TrackingMessage message) throws Exception ;
}
