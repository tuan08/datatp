package net.datatp.util.stat;

import java.io.Serializable;

public class StatisticEntry implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String name ;
  private long   frequency ;
  private String relateTo ;
  private Object model ;

  public StatisticEntry(String name, String relateTo, long freq)  { 
    this.name = name ; 
    this.relateTo = relateTo ;
    this.frequency = freq ; 
  }

  public String getName() { return this.name ; }

  public long getFrequency() { return this.frequency ; }

  public String getRelateTo(){ return this.relateTo; }

  public void incr(long value) { this.frequency += value ; }

  public <T> T getModel() { return (T) model ; }
  public void  setModel(Object object) { this.model = object ; }
}