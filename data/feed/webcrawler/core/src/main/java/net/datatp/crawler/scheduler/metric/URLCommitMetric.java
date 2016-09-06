package net.datatp.crawler.scheduler.metric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.datatp.util.text.DateUtil;
import net.datatp.util.text.TabularFormater;

public class URLCommitMetric implements Serializable {
  private static final long serialVersionUID = 1L;

  private long time ;
  private long execTime ;
  private int  commitURLCount = 0 ;
  private int  newURLFoundCount = 0 ;

  public URLCommitMetric() {
  }

  public URLCommitMetric(long time, long execTime, int processCount, int newURLFoundCount) {
    this.time = time; 
    this.execTime = execTime ;
    this.commitURLCount = processCount ;
    this.newURLFoundCount = newURLFoundCount ;
  }

  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }

  public long getExecTime() { return this.execTime ; }
  public void setExecTime(long time) { this.execTime = time; }

  public int  getCommitURLCount() { return commitURLCount; }
  public void setCommitURLCount(int commitCount) { this.commitURLCount = commitCount; }

  public int  getNewURLFoundCount() { return newURLFoundCount; }
  public void setNewURLFoundCount(int newURLFoundCount) { this.newURLFoundCount = newURLFoundCount; }

  public void readFields(DataInput in) throws IOException {
    time = in.readLong() ;
    execTime = in.readLong() ;
    commitURLCount = in.readInt() ;
    newURLFoundCount = in.readInt() ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeLong(time) ;
    out.writeLong(execTime) ;
    out.writeInt(commitURLCount) ;
    out.writeInt(newURLFoundCount);
  }

  static public String formatURLCommitInfosAsText(List<URLCommitMetric> holder) {
    String[] header = {
        "Time", "Exec Time", "Commit URL", "New URL"
    } ;
    TabularFormater formatter = new TabularFormater(header) ;
    Iterator<URLCommitMetric> i = holder.iterator() ;
    while(i.hasNext()) {
      URLCommitMetric sel = i.next() ;
      formatter.addRow(
        DateUtil.asCompactDateTime(sel.getTime()), sel.getExecTime(), sel.getCommitURLCount(),
        sel.getNewURLFoundCount()
      );
    }
    return formatter.getFormattedText() ; 
  }
}