package net.datatp.model.message;

import java.util.ArrayList;
import java.util.List;

public class MessageTracking {
  private String        source;
  private int           trackId;
  private List<TimeLog> timeLogs;
  
  public MessageTracking() {}
  
  public MessageTracking(String  source, int trackId) {
    this.source = source;
    this.trackId = trackId;
  }
  
  public String getSource() { return source; }
  public void   setSource(String source) { this.source = source; }

  public int    getTrackId() { return trackId; }
  public void   setTrackId(int trackId) { this.trackId = trackId; }

  public List<TimeLog> getTimeLogs() { return timeLogs; }
  public void setTimeLogs(List<TimeLog> timeLogs) { this.timeLogs = timeLogs; }
  
  public void addTimeLog(String name, long start, long end) {
    if(timeLogs == null) timeLogs = new ArrayList<>();
    timeLogs.add(new TimeLog(name, start, end)) ;
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("partition=").append(source).append(", ").
      append("trackId=").append(trackId);
    return b.toString();
  }
  
  static public class TimeLog {
    private String name;
    private long   start ;
    private long   end ;
    
    public TimeLog() { }
    
    public TimeLog(String name, long start, long end) {
      this.name  = name;
      this.start = start;
      this.end   = end ;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getStart() { return start; }
    public void setStart(long start) { this.start = start; }
    
    public long getEnd() { return end; }
    public void setEnd(long end) { this.end = end; }
  }
}
