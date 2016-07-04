package net.datatp.jms.queue;

import java.io.Serializable;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueueInfo implements Serializable {
  private QueueCallInfo enqueueInfo ;
  private QueueCallInfo dequeueInfo ;

  public QueueCallInfo getEnqueueInfo() { return enqueueInfo; }
  public void setEnqueueInfo(QueueCallInfo enqueueInfo) { this.enqueueInfo = enqueueInfo; }

  public QueueCallInfo getDequeueInfo() { return dequeueInfo; }
  public void setDequeueInfo(QueueCallInfo dequeueInfo) { this.dequeueInfo = dequeueInfo; }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    if(enqueueInfo != null) b.append(enqueueInfo.toString()).append("\n") ;
    if(dequeueInfo != null) b.append(dequeueInfo.toString());
    return b.toString() ;
  }
}