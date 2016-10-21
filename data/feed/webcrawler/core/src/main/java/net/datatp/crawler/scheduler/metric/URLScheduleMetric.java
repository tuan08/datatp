package net.datatp.crawler.scheduler.metric;

import java.io.Serializable;

import net.datatp.crawler.urldb.URLDatum;

public class URLScheduleMetric implements Serializable {
  private static final long serialVersionUID = 1L;

  private long time;
  private long execTime;
  private long urlCount              = 0;
  private long urlListCount          = 0;
  private long urlDetailCount        = 0;
  private long urlUncategorizedCount = 0;
  private long scheduleCount         = 0;
  private long delayScheduleCount    = 0;
  private long pendingCount          = 0;
  private long expiredPendingCount   = 0;
  private long waitingCount          = 0;
  private long errorCount            = 0;

  public URLScheduleMetric() {} 
  
  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }
  
  public long getExecTime() { return this.execTime ; }
  public void setExecTime(long time) { this.execTime = time ;} 
  
  public long getUrlCount() { return urlCount; }
  public void setUrlCount(long urlCount) { this.urlCount = urlCount; }

  public long getUrlListCount() { return urlListCount; }
  public void setUrlListCount(long urlListCount) { this.urlListCount = urlListCount; }

  public long getUrlDetailCount() { return urlDetailCount; }
  public void setUrlDetailCount(long urlDetailCount) { this.urlDetailCount = urlDetailCount; }

  public long getUrlUncategorizedCount() { return urlUncategorizedCount; }
  public void setUrlUncategorizedCount(long urlUncategorizedCount) { this.urlUncategorizedCount = urlUncategorizedCount; }

  public long getScheduleCount() { return scheduleCount; }
  public void setScheduleCount(long scheduleCount) { this.scheduleCount = scheduleCount; }
  
  public long getDelayScheduleCount() { return delayScheduleCount; }
  public void setDelayScheduleCount(long delayScheduleCount) { this.delayScheduleCount = delayScheduleCount; }
  
  public long getPendingCount() { return pendingCount; }
  public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
  
  public long getExpiredPendingCount() { return expiredPendingCount; }
  public void setExpiredPendingCount(long expiredPendingCount) { this.expiredPendingCount = expiredPendingCount; }

  public long getWaitingCount() { return waitingCount; }
  public void setWaitingCount(long waitingCount) { this.waitingCount = waitingCount; }

  public long getErrorCount() { return errorCount; }
  public void setErrorCount(long errorCount) { this.errorCount = errorCount; }

  public boolean isChangedCompareTo(URLScheduleMetric info) {
    if(info == null) return true;
    if(scheduleCount > 0) return true;
    if(urlCount != info.urlCount || delayScheduleCount != info.delayScheduleCount || 
       pendingCount != info.pendingCount || waitingCount != info.waitingCount) {
      return true;
    }
    return false;
  }
  
  public void log(URLDatum datum) {
    urlCount++ ;
    if(datum.getPageType() == URLDatum.PAGE_TYPE_LIST) urlListCount++;
    else if(datum.getPageType() == URLDatum.PAGE_TYPE_DETAIL) urlDetailCount++;
    else urlUncategorizedCount++;
  }
  
  public void logDelay(int incr) { this.delayScheduleCount += incr; }
  
  public void logSchedule(int incr) { this.scheduleCount += incr; }
  
  public void logPending(int incr) { this.pendingCount += incr; }
  
  public void logExpiredPending(int incr) { this.expiredPendingCount += incr; }
  
  public void logWaiting(int incr) { this.waitingCount += incr; }
  
  public void logError(int incr) { this.errorCount += incr; }
}
