package net.datatp.util.dataformat;

import java.io.PrintStream;

import net.datatp.util.stat.Statistics;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DataMultiFileReporter<T> {
  private String location ;
  private Class<T> type ;
  private Statistics map ;

  public DataMultiFileReporter(String loc, Class<T> type) {
    this.location = loc ;
    this.type = type ;
    this.map = new Statistics() ;
  }

  public void process() throws Exception {
    map.clear() ;
    DataMultiFileReader reader = new DataMultiFileReader(location) ;
    T object  = null ;
    while((object = reader.next(type)) != null) {
      log(object) ;
    }
    reader.close() ;
  }

  protected void log(T object) {
    map.incr("Statistic", "all", 1) ;
  }

  public void report(PrintStream out) {
    map.report(out);
  }
}
