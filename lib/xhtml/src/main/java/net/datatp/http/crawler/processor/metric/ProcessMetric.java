package net.datatp.http.crawler.processor.metric;

import java.io.Serializable;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ProcessMetric implements Serializable {
  private int  processCount ;
  private int  htmlProcessCount ;
  private long sumHtmlProcessTime;
  private long sumProcessTime ;

  public int  getProcessCount() { return processCount; }
  public void setProcessCount(int processCount) { this.processCount = processCount; }
  synchronized public void incrProcessCount() { processCount++ ; }


  public int  getHtmlProcessCount() { return htmlProcessCount; }
  public void setHtmlProcessCount(int htmlProcessCount) { this.htmlProcessCount = htmlProcessCount; }
  public void incHtmlProcessCount() { htmlProcessCount++ ; }

  public long getSumHtmlProcessTime() { return sumHtmlProcessTime; }
  public void setSumHtmlProcessTime(long sumHtmlProcessTime) { this.sumHtmlProcessTime = sumHtmlProcessTime;} 
  synchronized public void addSumHtmlProcessTime(long time) {
    sumHtmlProcessTime += time ;
  }

  public long getSumProcessTime() { return sumProcessTime; }
  public void setSumProcessTime(long sumProcessTime) { this.sumProcessTime = sumProcessTime;} 
  synchronized public void addSumProcessTime(long time) {
    sumProcessTime += time ;
  }
}