package net.datatp.crawler.distributed.master;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;

import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;

public class CrawlerMasterInfo implements Serializable {
  final static public int MAX_INFO_SIZE =  100 ;

  final static public int STOP_STATUS = 0 ;
  final static public int RUNNING_STATUS = 1 ;

  private long startTime ;
  private int  status ;
  private LinkedList<URLScheduleMetric> sheduleMetric = new LinkedList<URLScheduleMetric>() ;
  private LinkedList<URLCommitMetric>   fetchURLCommitMetric = new LinkedList<URLCommitMetric>() ;

  public CrawlerMasterInfo() {

  }

  public CrawlerMasterInfo(boolean loadJVMInfo) {
    if(loadJVMInfo) {
    }
  }

  public long getStartTime() { return startTime; }
  public void setStartTime(long startTime) { this.startTime = startTime ; }

  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }

  public String getStatusInfo() {
    if(status == RUNNING_STATUS) return "RUNNING" ;
    return "STOP" ;
  }

  public URLScheduleMetric[] getSheduleMetric() { 
    return sheduleMetric.toArray(new URLScheduleMetric[sheduleMetric.size()]);
  }

  public void addSheduleMetric(URLScheduleMetric info) {
    sheduleMetric.addFirst(info) ;
    if(this.sheduleMetric.size() == MAX_INFO_SIZE) {
      this.sheduleMetric.removeLast() ;
    }
  }

  public URLCommitMetric[] getFetchURLCommitMetrics() {
    return fetchURLCommitMetric.toArray(new URLCommitMetric[fetchURLCommitMetric.size()]);
  }

  public void addFetchDataProcessInfo(URLCommitMetric info) {
    fetchURLCommitMetric.addFirst(info);
    if(fetchURLCommitMetric.size() == MAX_INFO_SIZE) {
      fetchURLCommitMetric.removeLast() ;
    }
  }

  public void report(PrintStream out) {
    out.println("############SCHEDULE INFO##################################") ;
    String[] scheduleHeader   =  { "url", "schedule", "delay", "pending", "waiting"	} ;
    int[] scheduleHeaderWidth =  {    10,         10,      10,        10,        10 } ;
    printRow(out, scheduleHeader, scheduleHeaderWidth) ;
    for(URLScheduleMetric sel :  this.sheduleMetric) {
      String[] value = { 
          Integer.toString(sel.getUrlCount()),
          Integer.toString(sel.getScheduleCount()),
          Integer.toString(sel.getDelayScheduleCount()),
          Integer.toString(sel.getPendingCount()),
          Integer.toString(sel.getWaitingCount()),
      } ;
      printRow(out, value, scheduleHeaderWidth) ;
    }
    out.println("############PROCESS INFO##################################") ;
    String[] processHeader   =  { "url", "new url", "new url list", "new url detail"} ;
    int[] processHeaderWidth =  {    10,        10,             15,              15} ;
    printRow(out, processHeader, processHeaderWidth) ;
    for(URLCommitMetric sel : fetchURLCommitMetric) {
      String[] value = { 
          Integer.toString(sel.getCommitURLCount()),
          Integer.toString(sel.getNewURLFoundCount()),
          Integer.toString(sel.getNewURLTypeList()),
          Integer.toString(sel.getNewURLTypeDetail()),
      } ;
      printRow(out, value, processHeaderWidth) ;
    }
  }

  private void printRow(PrintStream out, String[] cell, int[] width) {
    for(int i = 0; i < cell.length; i++) {
      out.print(cell[i]) ;
      for(int j = cell[i].length(); j < width[i]; j++) out.print(' ') ;
    }
    out.println() ;
  }
}
