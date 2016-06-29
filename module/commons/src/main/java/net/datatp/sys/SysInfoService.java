package net.datatp.sys;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import com.google.inject.Singleton;

import net.datatp.model.sys.FileStore;
import net.datatp.model.sys.GC;
import net.datatp.model.sys.LoadedClass;
import net.datatp.model.sys.Memory;
import net.datatp.model.sys.OS;
import net.datatp.model.sys.ThreadCount;

@SuppressWarnings({"restriction"})
@Singleton
public class SysInfoService {
  
  private Map<String, GC> previousGCs = new HashMap<String, GC>() ;
  
  public Memory[] getMemory() {
    MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    Memory heapMemory = new Memory("Heap", mbean.getHeapMemoryUsage());
    Memory nonHeapMemory = new Memory("NonHeap", mbean.getNonHeapMemoryUsage());
    return new Memory[] { heapMemory, nonHeapMemory } ;
  }
  
  
  public GC[] getGC() {
    List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
    GC[] gc = new GC[gcbeans.size()];
    for(int i = 0; i < gcbeans.size(); i++) {
      GarbageCollectorMXBean gcbean = gcbeans.get(i) ;
      gc[i] = new GC(gcbean);
      GC prevGCInfo = previousGCs.get(gc[i].getName());
      if(prevGCInfo != null) {
        gc[i].setDiffCollectionCount(gc[i].getCollectionCount() - prevGCInfo.getCollectionCount());
      }else{
        gc[i].setDiffCollectionCount(gc[i].getCollectionCount());
      }
      previousGCs.put(gc[i].getName(), gc[i]);
    }
    return gc;
  }
  
  public ThreadCount getThreadCount() { return new ThreadCount(ManagementFactory.getThreadMXBean());  }
  
  public LoadedClass getLoadedClass() { return new LoadedClass(ManagementFactory.getClassLoadingMXBean()); }
  
  public FileStore[] getFileStore() {
    FileSystemView fsv = FileSystemView.getFileSystemView();
    File[] drives = File.listRoots();
    List<FileStore> fsStoreInfo = new ArrayList<>();
    if (drives != null && drives.length > 0) {
      for (File aDrive : drives) {
        FileStore info = new FileStore();
        info.setName(aDrive.getAbsolutePath());
        info.setType(fsv.getSystemTypeDescription(aDrive));
        info.setTotal(aDrive.getTotalSpace());
        info.setUsed(aDrive.getTotalSpace() - aDrive.getFreeSpace());
        fsStoreInfo.add(info);
      }
    }
    return fsStoreInfo.toArray(new FileStore[fsStoreInfo.size()]);
  }
  
  public OS getOS() {
    return new OS(ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class));
  }
}