package net.datatp.model.sys;

import java.io.Serializable;
import java.lang.management.ClassLoadingMXBean;

import net.datatp.util.text.TabularFormater;

@SuppressWarnings("serial")
public class LoadedClass implements Serializable {
  private int    loadedClassCount;
  private long   totalLoadedClassCount;
  private long   unloadedClassCount;

 
  public LoadedClass() { }
  
  public LoadedClass(ClassLoadingMXBean clbean) {
    totalLoadedClassCount = clbean.getTotalLoadedClassCount();
    loadedClassCount      = clbean.getLoadedClassCount();
    unloadedClassCount    = clbean.getUnloadedClassCount();
  }

  public int getLoadedClassCount() { return loadedClassCount; }
  public void setLoadedClassCount(int loadedClassCount) {
    this.loadedClassCount = loadedClassCount;
  }

  public long getTotalLoadedClassCount() { return totalLoadedClassCount; }
  public void setTotalLoadedClassCount(long totalLoadedClassCount) {
    this.totalLoadedClassCount = totalLoadedClassCount;
  }

  public long getUnloadedClassCount() { return unloadedClassCount; }
  public void setUnloadedClassCount(long unloadedClassCount) {
    this.unloadedClassCount = unloadedClassCount;
  }
  
  static public String getFormattedText(LoadedClass ... clInfo) {
    String[] header = {"Total Loaded Class Count", "Loaded Class Count", "Unloaded Class Count"} ;
    TabularFormater formatter = new TabularFormater(header) ;
    for(LoadedClass sel : clInfo) {
      formatter.addRow(
        sel.getTotalLoadedClassCount(), 
        sel.getLoadedClassCount(),
        sel.getUnloadedClassCount());
    }
    return formatter.getFormattedText() ;
  }
}
