package net.datatp.tracking.es;

import java.net.UnknownHostException;
import java.util.List;
import java.util.TreeMap;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.tracking.TrackingMessage;
import net.datatp.tracking.TrackingMessageReport;
import net.datatp.tracking.TrackingRegistry;
import net.datatp.util.text.StringUtil;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryException;

public class ESTrackingValidator {
  private int              numOfChunk ;
  private long             checkPeriod = 5000;
  private TrackingRegistry trackingRegistry;
  private TreeMap<String, TrackingMessageReport> validatorReports = new TreeMap<>();
  private ESObjectClient<TrackingMessage> esObjecClient;
  private CheckThread checkThread;
  
  public ESTrackingValidator(Registry registry, String reportPath, int numOfChunk, String esConnects, String index, long checkPeriod) throws RegistryException, UnknownHostException {
    this.numOfChunk       = numOfChunk;
    this.checkPeriod      = checkPeriod ;
    this.trackingRegistry = new TrackingRegistry(registry, reportPath, true);
    
    String[] esConnect = StringUtil.toStringArray(esConnects);
    esObjecClient = new ESObjectClient<TrackingMessage>(new ESClient(esConnect), index, TrackingMessage.class) ;
  }

  public void start() {
    checkThread = new CheckThread();
    checkThread.start();
  }
  
  public void waitForTermination(long timeout) throws InterruptedException {
    checkThread.waitForTermination(timeout);
  }
  
  public void stop() {
    checkThread.terminated = true;
    checkThread.interrupt();
  }
  
  public boolean check() throws Exception {
    List<TrackingMessageReport> generatorReports = trackingRegistry.getGeneratorReports();
    boolean finished = true ;
    for(int i = 0; i < generatorReports.size(); i++) {
      TrackingMessageReport genReport = generatorReports.get(i);
      TrackingMessageReport validatorReport  = validatorReports.get(genReport.reportName());
      if(validatorReport == null) {
        validatorReport = new TrackingMessageReport(genReport.getVmId(), genReport.getChunkId(), genReport.getNumOfMessage());
        validatorReports.put(validatorReport.reportName(), validatorReport);
      }
      if(validatorReport.isComplete()) continue;
      
      int count = (int)esObjecClient.getQueryExecutor().countByMatchTerm("chunkId", validatorReport.getChunkId());
      validatorReport.setProgress(count);
      validatorReport.setNoLostTo(count);
      trackingRegistry.saveValidatorReport(validatorReport);
      finished = false;
    }
    if(numOfChunk > validatorReports.size()) finished = false;
    return finished;
  }
  
  public class CheckThread extends Thread {
    boolean terminated = false;
    
    public void run() {
      while(!terminated) {
        try {
          Thread.sleep(checkPeriod);
          boolean finished = check();
          if(finished) terminated = true;
        } catch(InterruptedException ex) {
          terminated = true;
        } catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      notifiyTermination();
    }
    
    synchronized public void waitForTermination(long timeout) throws InterruptedException {
      wait(timeout);
    }
    
    synchronized public void notifiyTermination() {
      notifyAll();
    }
  }
}
