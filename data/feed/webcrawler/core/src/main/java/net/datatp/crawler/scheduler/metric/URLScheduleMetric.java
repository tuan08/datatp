package net.datatp.crawler.scheduler.metric;

import java.io.Serializable;

public class URLScheduleMetric implements Serializable {
  private long time ;
  private long execTime; 
  private int  urlCount = 0;
  private int  scheduleCount = 0;
  private int  delayScheduleCount = 0 ;
  private int  pendingCount = 0;
  private int  waitingCount = 0 ;
  
  public URLScheduleMetric() {} 
  
  public URLScheduleMetric(long time, long execTime, int urlCount, int scheduleCount, 
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

  public boolean isChangedCompareTo(URLScheduleMetric info) {
    if(info == null) return true;
    if(scheduleCount > 0) return true;
    if(urlCount != info.urlCount || delayScheduleCount != info.delayScheduleCount || 
       pendingCount != info.pendingCount || waitingCount != info.waitingCount) {
      return true;
    }
    return false;
  }
}
