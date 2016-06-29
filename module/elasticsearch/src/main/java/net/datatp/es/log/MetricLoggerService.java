package net.datatp.es.log;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import net.datatp.sys.RuntimeEnv;
import net.datatp.yara.Counter;
import net.datatp.yara.Meter;
import net.datatp.yara.MetricRegistry;
import net.datatp.yara.Timer;
import net.datatp.yara.snapshot.CounterSnapshot;
import net.datatp.yara.snapshot.MetterSnapshot;
import net.datatp.yara.snapshot.TimerSnapshot;

public class MetricLoggerService  extends ObjectLoggerService {
  
  @Inject
  private MetricRegistry            metricRegistry;
  private String                    serverName;
  private MetricInfoCollectorThread metricCollectorThread;

  public MetricLoggerService() {}
  
  public MetricLoggerService(MetricRegistry metricRegistry, String serverName, String bufferBaseDir, String[] esConnect) throws Exception {
    this.metricRegistry = metricRegistry;
    init(serverName, bufferBaseDir, esConnect);
  }
  
  @Inject
  public void onInit(RuntimeEnv runtimeEnv) throws Exception {
    serverName = runtimeEnv.getVMName();
    String bufferBaseDir = runtimeEnv.getDataDir() + "/buffer/metric-log" ;
    String[] esConnect = { "elasticsearch-1:9300" };
    init(serverName, bufferBaseDir, esConnect);
  }
  
  private void init(String serverName, String bufferBaseDir, String[] esConnect) throws Exception {
    init(esConnect, bufferBaseDir, 25000);
    add(CounterSnapshot.class, "metric-counter");
    add(TimerSnapshot.class,   "metric-timer");
    add(MetterSnapshot.class,  "metric-metter");
    
    metricCollectorThread = new MetricInfoCollectorThread();
    metricCollectorThread.start();
  }

  
  
  public class MetricInfoCollectorThread extends Thread {
    public void run() {
      try {
        while(true) {
          Map<String, Counter> counters = metricRegistry.getCounters() ;
          for(Map.Entry<String, Counter> entry : counters.entrySet()) {
            CounterSnapshot counterSnapshot = new CounterSnapshot(serverName, entry.getValue());
            addLog(counterSnapshot.uniqueId(), counterSnapshot);
          }
          Map<String, Timer>  timers = metricRegistry.getTimers();
          for(Map.Entry<String, Timer> entry : timers.entrySet()) {
            TimerSnapshot timerSnapshot = new TimerSnapshot(serverName, entry.getValue(), TimeUnit.MILLISECONDS);
            addLog(timerSnapshot.uniqueId(), timerSnapshot);
          }
          Map<String, Meter>  meters = metricRegistry.getMeters();
          for(Map.Entry<String, Meter> entry : meters.entrySet()) {
            MetterSnapshot meterSnapshot = new MetterSnapshot(serverName, entry.getValue());
            addLog(meterSnapshot.uniqueId(), meterSnapshot);
          }
          Thread.sleep(30000);
        }
      } catch (InterruptedException e) {
      }
    }
  }
}
