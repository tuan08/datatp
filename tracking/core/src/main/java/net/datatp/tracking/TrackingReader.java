package net.datatp.tracking;

abstract public class TrackingReader {
  public void onInit(TrackingRegistry registry) throws Exception {
  }
 
  public void onDestroy(TrackingRegistry registry) throws Exception{
  }
  
  abstract public TrackingMessage next() throws Exception ;
}
