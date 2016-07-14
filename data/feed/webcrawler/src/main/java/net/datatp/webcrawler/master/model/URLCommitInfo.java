package net.datatp.webcrawler.master.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.datatp.util.text.DateUtil;
import net.datatp.util.text.TabularFormater;

public class URLCommitInfo implements Serializable {
  private long time ;
  private long execTime ;
  private int  commitURLCount = 0 ;
  private int  newURLFoundCount = 0 ;
  private int  newURLTypeList = 0 ;
  private int  newURLTypeDetail = 0 ;

  public URLCommitInfo() {
  }

  public URLCommitInfo(long time, long execTime, int processCount, 
                       int newURLFoundCount, int newURLTypeList, 
                       int newURLTypeDetail) {
    this.time = time; 
    this.execTime = execTime ;
    this.commitURLCount = processCount ;
    this.newURLFoundCount = newURLFoundCount ;
    this.newURLTypeList = newURLTypeList ;
    this.newURLTypeDetail = newURLTypeDetail ;
  }

  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }

  public long getExecTime() { return this.execTime ; }
  public void setExecTime(long time) { this.execTime = time; }

  public int  getCommitURLCount() { return commitURLCount; }
  public void setCommitURLCount(int commitCount) { this.commitURLCount = commitCount; }

  public int  getNewURLFoundCount() { return newURLFoundCount; }
  public void setNewURLFoundCount(int newURLFoundCount) { this.newURLFoundCount = newURLFoundCount; }

  public int getNewURLTypeList() { return newURLTypeList; }
  public void setNewURLTypeList(int newURLTypeList) { this.newURLTypeList = newURLTypeList; }

  public int getNewURLTypeDetail() { return newURLTypeDetail; }
  public void setNewURLTypeDetail(int newURLTypeDetail) { this.newURLTypeDetail = newURLTypeDetail; }

  public void readFields(DataInput in) throws IOException {
    time = in.readLong() ;
    execTime = in.readLong() ;
    commitURLCount = in.readInt() ;
    newURLFoundCount = in.readInt() ;
    newURLTypeList = in.readInt() ;
    newURLTypeDetail = in.readInt() ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeLong(time) ;
    out.writeLong(execTime) ;
    out.writeInt(commitURLCount) ;
    out.writeInt(newURLFoundCount);
    out.writeInt(newURLTypeList) ;
    out.writeInt(newURLTypeDetail) ;
  }

  static public String formatURLCommitInfosAsText(List<URLCommitInfo> holder) {
    String[] header = {
        "Time", "Exec Time", "Commit URL", "New URL", "URL List", "URL Detail"
    } ;
    TabularFormater formatter = new TabularFormater(header) ;
    Iterator<URLCommitInfo> i = holder.iterator() ;
    while(i.hasNext()) {
      URLCommitInfo sel = i.next() ;
      formatter.addRow(
          DateUtil.asCompactDateTime(sel.getTime()), sel.getExecTime(), sel.getCommitURLCount(),
          sel.getNewURLFoundCount(), sel.getNewURLTypeList(), sel.getNewURLTypeDetail()
          );
    }
    return formatter.getFormattedText() ; 
  }
}