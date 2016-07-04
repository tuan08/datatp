package net.datatp.webcrawler.master;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

import net.datatp.webcrawler.urldb.URLDatumCommitInfo;
import net.datatp.webcrawler.urldb.URLDatumScheduleInfo;

public class CrawlerMasterInfo implements Writable {
  final static public int MAX_INFO_SIZE =  100 ;

  final static public int STOP_STATUS = 0 ;
  final static public int RUNNING_STATUS = 1 ;

  private long startTime ;
  private int  status ;
  private LinkedList<URLDatumScheduleInfo> sheduleInfo = new LinkedList<URLDatumScheduleInfo>() ;
  private LinkedList<URLDatumCommitInfo> fetchDataProcessInfo = new LinkedList<URLDatumCommitInfo>() ;
  private MapWritable jvmInfo = new MapWritable() ;

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

  public MapWritable getJVMInfo() { return this.jvmInfo ; }

  public String getStatusInfo() {
    if(status == RUNNING_STATUS) return "RUNNING" ;
    return "STOP" ;
  }

  public URLDatumScheduleInfo[] getSheduleInfo() { 
    return sheduleInfo.toArray(new URLDatumScheduleInfo[sheduleInfo.size()]);
  }

  public void addSheduleInfo(URLDatumScheduleInfo info) {
    sheduleInfo.addFirst(info) ;
    if(this.sheduleInfo.size() == MAX_INFO_SIZE) {
      this.sheduleInfo.removeLast() ;
    }
  }

  public URLDatumCommitInfo[] getFetchDataProcessInfo() {
    return fetchDataProcessInfo.toArray(new URLDatumCommitInfo[fetchDataProcessInfo.size()]);
  }

  public void addFetchDataProcessInfo(URLDatumCommitInfo info) {
    fetchDataProcessInfo.addFirst(info);
    if(fetchDataProcessInfo.size() == MAX_INFO_SIZE) {
      fetchDataProcessInfo.removeLast() ;
    }
  }

  public void readFields(DataInput in) throws IOException {
    this.startTime = in.readLong() ;
    this.status = in.readInt() ;
    int size = in.readInt() ;
    for(int i = 0; i < size; i++) {
      URLDatumScheduleInfo info = new URLDatumScheduleInfo();
      info.readFields(in);
      sheduleInfo.add(info) ;
    }
    size = in.readInt() ;
    for(int i = 0; i < size; i++) {
      URLDatumCommitInfo info = new URLDatumCommitInfo();
      info.readFields(in);
      fetchDataProcessInfo.add(info) ;
    }
    jvmInfo.readFields(in) ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeLong(startTime) ;
    out.writeInt(status) ;

    out.writeInt(this.sheduleInfo.size()) ;
    Iterator<URLDatumScheduleInfo>  scheduleInfoItr = sheduleInfo.iterator() ;
    while(scheduleInfoItr.hasNext()) {
      scheduleInfoItr.next().write(out) ;
    }

    out.writeInt(this.fetchDataProcessInfo.size()) ;
    Iterator<URLDatumCommitInfo> fetchDataProcessInfoItr = fetchDataProcessInfo.iterator() ;
    while(fetchDataProcessInfoItr.hasNext()) {
      fetchDataProcessInfoItr.next().write(out) ;
    }
    jvmInfo.write(out) ;
  }

  public void report(PrintStream out) {
    out.println("############SCHEDULE INFO##################################") ;
    String[] scheduleHeader   =  { "url", "schedule", "delay", "pending", "waiting"	} ;
    int[] scheduleHeaderWidth =  {    10,         10,      10,        10,        10 } ;
    printRow(out, scheduleHeader, scheduleHeaderWidth) ;
    for(URLDatumScheduleInfo sel :  this.sheduleInfo) {
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
    for(URLDatumCommitInfo sel : fetchDataProcessInfo) {
      String[] value = { 
          Integer.toString(sel.getURLCommitCount()),
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
