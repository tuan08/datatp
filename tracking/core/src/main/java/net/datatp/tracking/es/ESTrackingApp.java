package net.datatp.tracking.es;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import net.datatp.tracking.PropertiesConfig;
import net.datatp.tracking.TrackingGeneratorService;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.zk.RegistryImpl;

public class ESTrackingApp {
  @Parameter(names = "--zk-connect", description = "The zk connect string")
  private String zkConnects = "localhost:2181";
  
  @Parameter(names = "--es-connect", description = "The zk connect string")
  private String esConnects = "localhost:9300";

  @Parameter(names = "--es-index", description = "The number of the replications")
  private String  esIndex = "tracking-message";

  @Parameter(names = "--tracking-path", description = "The zk connect string")
  private String trackingPath = "/tracking";
  
  @Parameter(names = "--num-of-chunk", description = "The number of chunks")
  private int numOfChunk = 10;
  
  @Parameter(names = "--num-of-message-per-chunk", description = "The number of messages per chunk")
  private int numOfMessagePerChunk = 100;
  
  @Parameter(names = "--check-period", description = "Check period for the validator")
  private long checkPeriod = 5000;
  
  @Parameter(names = "--max-run-time", description = "The max run time for the application")
  private long maxRunTime = 25000;
  
  private Registry registry ;
  private TrackingGeneratorService generatorService;
  
  private ESTrackingValidator      validatorService;
  
  public ESTrackingApp(String[] args) throws Exception {
    String configFile = System.getProperty("tracking.config");
    if(configFile != null) {
      PropertiesConfig config = new PropertiesConfig();
      config.load(new FileInputStream(configFile));
      zkConnects   = config.getProperty("zk.connect", zkConnects);
      
      esConnects   = config.getProperty("es.connect", esConnects);
      esIndex      = config.getProperty("es.index", esIndex);
      
      trackingPath = config.getProperty("tracking.path", trackingPath);
      numOfChunk   = config.getPropertyAsInt("tracking.num-of-chunk", numOfChunk);
      numOfMessagePerChunk = config.getPropertyAsInt("tracking.num-of-message-per-chunk", numOfMessagePerChunk);
      maxRunTime = config.getPropertyAsLong("tracking.max-run-time", maxRunTime);
    }
    System.out.println("esConnects = " + esConnects + ", index = " + esIndex);
    new JCommander(this, args);
    RegistryConfig regConfig = RegistryConfig.getDefault();
    regConfig.setConnect(zkConnects);
    registry =  new RegistryImpl(regConfig).connect() ;
    
    generatorService  = new TrackingGeneratorService(registry, trackingPath, numOfChunk, numOfMessagePerChunk);
    String[] writerConfig = {
        "--es-connect", esConnects, "--es-index", esIndex
    };
    generatorService.addWriter(new ESTrackingWriter(writerConfig));
  
    validatorService = new ESTrackingValidator(registry, trackingPath, numOfChunk, esConnects, esIndex, checkPeriod) ;
  }
  
  public void start() throws Exception {
    generatorService.start();
    validatorService.start();
  }
  
  public void waitForTermination() throws Exception {
    long startTime = System.currentTimeMillis() ;
    generatorService.awaitForTermination(maxRunTime, TimeUnit.MILLISECONDS);
    generatorService.shutdown();
    long duration = System.currentTimeMillis() - startTime; 
    if(maxRunTime - duration > 0) {
      validatorService.waitForTermination(maxRunTime - duration);
    }
    validatorService.stop();
  }
  
  public void shutdown() throws Exception {
    registry.shutdown();
  }
  
  public void run() throws Exception {
    start();
    waitForTermination();
    shutdown();
  }
  
  static public void main(String[] args) throws Exception {
    ESTrackingApp app = new ESTrackingApp(args);
    app.run();
  }
}
