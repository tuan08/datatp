package net.datatp.http.crawler.site;

import java.io.Serializable;

public class SiteScheduleStat implements Serializable {
  private int scheduleCount ;
  private int processCount ;

  public void addScheduleCount(int num) {  this.scheduleCount +=  num ; }
  public int getScheduleCount() { return this.scheduleCount ; }

  public boolean canSchedule(int maxSchedulePerSite, SiteContext ctx) {
    maxSchedulePerSite = maxSchedulePerSite * ctx.getMaxConnection() ;
    if(scheduleCount - processCount < maxSchedulePerSite) return true ;
    return false ;
  }

  public int getMaxSchedule(int maxSchedulePerSite, SiteContext ctx) {
    maxSchedulePerSite = maxSchedulePerSite * ctx.getMaxConnection() ;
    int inqueue = scheduleCount - processCount ;
    int ret = maxSchedulePerSite - inqueue ;
    return ret ;
  }

  public void addProcessCount(int num) { processCount +=  num ; }

  public int getProcessCount() { return processCount ; }
}