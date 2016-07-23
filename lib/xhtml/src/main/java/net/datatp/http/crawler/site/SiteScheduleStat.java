package net.datatp.http.crawler.site;

import java.io.Serializable;

public class SiteScheduleStat implements Serializable {
  private static final long serialVersionUID = 1L;

  private int scheduleCount ;
  private int processCount ;

  public void addScheduleCount(int num) {  this.scheduleCount +=  num ; }
  public int getScheduleCount() { return this.scheduleCount ; }

  public boolean canSchedule(int maxSchedulePerSite, int maxConn) {
    maxSchedulePerSite = maxSchedulePerSite * maxConn ;
    if(scheduleCount - processCount < maxSchedulePerSite) return true ;
    return false ;
  }
  
  public int getMaxSchedule(int maxSchedulePerSite, int maxConn) {
    maxSchedulePerSite = maxSchedulePerSite * maxConn ;
    int inqueue = scheduleCount - processCount ;
    int ret = maxSchedulePerSite - inqueue ;
    return ret ;
  }

  public void addProcessCount(int num) { processCount +=  num ; }

  public int getProcessCount() { return processCount ; }
}