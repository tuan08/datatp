package net.datatp.webcrawler.master.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class URLScheduleInfo implements Serializable {
  private long time ;
  private long execTime; 
  private int  urlCount = 0;
  private int  scheduleCount = 0;
  private int  delayScheduleCount = 0 ;
  private int  pendingCount = 0;
  private int  waitingCount = 0 ;
  
  public URLScheduleInfo() {} 
  
  public URLScheduleInfo(long time, long execTime, int urlCount, int scheduleCount, 
  		                        int delayScheduleCount, int pendingCount, int waitingCount) {
    this.time = time; 
    this.execTime = execTime ;
    this.urlCount = urlCount; 
    this.scheduleCount = scheduleCount ;
    this.delayScheduleCount = delayScheduleCount ;
    this.pendingCount = pendingCount ;
    this.waitingCount = waitingCount ;
  }
  
  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }
  
  public long getExecTime() { return this.execTime ; }
  public void setExecTime(long time) { this.execTime = time ;} 
  
  public int getUrlCount() { return urlCount; }
  public void setUrlCount(int urlCount) { this.urlCount = urlCount; }
  
  public int getScheduleCount() { return scheduleCount; }
  public void setScheduleCount(int scheduleCount) { this.scheduleCount = scheduleCount; }
  
  public int getDelayScheduleCount() { return delayScheduleCount; }
  public void setDelayScheduleCount(int delayScheduleCount) { this.delayScheduleCount = delayScheduleCount; }
  
  public int getPendingCount() { return pendingCount; }
  public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
  
  public int getWaitingCount() { return waitingCount; }
  public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }

  public void readFields(DataInput in) throws IOException {
    this.time = in.readLong() ;
    this.execTime = in.readLong() ;
    this.urlCount = in.readInt() ;
    this.scheduleCount = in.readInt() ;
    this.delayScheduleCount = in.readInt() ;
    this.pendingCount = in.readInt() ;
    this.waitingCount = in.readInt() ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeLong(time) ;
    out.writeLong(execTime) ;
    out.writeInt(urlCount) ;
    out.writeInt(scheduleCount) ;
    out.writeInt(delayScheduleCount) ;
    out.writeInt(pendingCount) ;
    out.writeInt(waitingCount) ;
  }
}
