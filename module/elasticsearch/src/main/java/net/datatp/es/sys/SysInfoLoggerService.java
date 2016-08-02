package net.datatp.es.sys;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PreDestroy;

//import com.google.inject.Inject;
//import com.google.inject.Singleton;

import net.datatp.es.log.ObjectLoggerService;
import net.datatp.model.sys.Sys;
import net.datatp.sys.JHiccupMeter;
import net.datatp.sys.RuntimeEnv;
import net.datatp.sys.SysInfoService;

//@Singleton
public class SysInfoLoggerService extends ObjectLoggerService {
  private String                    serverName;
  private SysInfoService            sysInfoService;
  private JHiccupMeter              jhiccupMetter;
  private MetricInfoCollectorThread metricCollectorThread;
  private long                      logPeriod         = 15000;
  
  //nject
  public void onInit(RuntimeEnv runtimeEnv) throws Exception {
    this.serverName     = runtimeEnv.getVMName();
    this.sysInfoService = new SysInfoService();
    //Detect only the hiccup that has more than 50ms to save the cpu cycle
    jhiccupMetter = new JHiccupMeter(runtimeEnv.getVMName(), 50L /*resolutionMs*/); 
    String bufferBaseDir = runtimeEnv.getDataDir() + "/buffer/sys-info" ;
    String[] esConnect = runtimeEnv.getEsConnects();
    init(esConnect, bufferBaseDir, 25000);
   
    add(Sys.class, "sys-info");
    
    metricCollectorThread = new MetricInfoCollectorThread();
    metricCollectorThread.start();
  }
  
  @PreDestroy
  public void onDestroy() throws IOException {
    System.err.println("OSMonitorLoggerService: onDestroy.........................");
    metricCollectorThread.interrupt();
    close();
  }
  
  public void setLogPeriod(long period) { this.logPeriod = period; }
  
  public void log() {
    Sys info = new Sys();
    info.setTimestamp(new Date());
    info.setHost(serverName);
    info.add("sys-storage", sysInfoService.getFileStore());
    info.add("sys-gc",      sysInfoService.getGC());
    info.add("sys-mem",     sysInfoService.getMemory());
    info.add("sys-os",      sysInfoService.getOS());
    info.add("sys-thread",  sysInfoService.getThreadCount());
    info.add("sys-jhicup",  jhiccupMetter.getHiccup());
    addLog(info.uniqueId(), info);
  }
  
  public class MetricInfoCollectorThread extends Thread {
    public void run() {
      try {
        while(true) {
          log();
          Thread.sleep(logPeriod);
        }
      } catch(InterruptedException e) {
        
      } catch(Throwable t) {
        t.printStackTrace();
      }
    }
  }
}
