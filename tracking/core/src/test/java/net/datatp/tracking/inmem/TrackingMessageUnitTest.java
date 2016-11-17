package net.datatp.tracking.inmem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.registry.Registry;
import net.datatp.registry.RegistryConfig;
import net.datatp.registry.RegistryException;
import net.datatp.registry.zk.RegistryImpl;
import net.datatp.tracking.TrackingGeneratorService;
import net.datatp.tracking.TrackingMessageReport;
import net.datatp.tracking.TrackingRegistry;
import net.datatp.tracking.TrackingValidatorService;
import net.datatp.tracking.inmem.InMemTrackingStorage;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
import net.datatp.zk.tool.server.EmbededZKServer;

public class TrackingMessageUnitTest {
  final static String WORKING_DIR           = "./build/working";
  final static String TRACKING_PATH         = "/tracking";
  final static int    NUM_OF_CHUNK          = 10;
  final static int    NUM_OF_MESG_PER_CHUNK = 500;
  
  private EmbededZKServer          zkServerLauncher;
  private Registry                 registry;
  private InMemTrackingStorage        trackingMessageDB;
  private TrackingGeneratorService generatorService;
  private TrackingValidatorService validatorService;
  
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("WARN");
    FileUtil.removeIfExist(WORKING_DIR, false);
    zkServerLauncher = new EmbededZKServer( WORKING_DIR + "/zookeeper") ;
    zkServerLauncher.start();
    registry =  new RegistryImpl(RegistryConfig.getDefault()).connect() ;
    
    trackingMessageDB = new InMemTrackingStorage();
    
    generatorService  = new TrackingGeneratorService(registry, TRACKING_PATH, NUM_OF_CHUNK, NUM_OF_MESG_PER_CHUNK);
    generatorService.addWriter(new InMemTrackingStorage.Writer(trackingMessageDB));
    
    validatorService = new TrackingValidatorService(registry, TRACKING_PATH, NUM_OF_MESG_PER_CHUNK);
    validatorService.addReader(new InMemTrackingStorage.Reader(trackingMessageDB));
  }
  
  @After
  public void teardown() throws RegistryException {
    registry.shutdown();
    zkServerLauncher.shutdown();
  }
  
  @Test
  public void testTracking() throws Exception {
    generatorService.start();
    validatorService.start();
    
    generatorService.awaitForTermination(10000, TimeUnit.MILLISECONDS);
    generatorService.shutdown();

    validatorService.awaitForTermination(10000, TimeUnit.MILLISECONDS);
    validatorService.shutdown();
    
    TrackingRegistry trackingRegistry = generatorService.getTrackingRegistry();
    List<TrackingMessageReport> generatedReports = trackingRegistry.getGeneratorReports();
    List<TrackingMessageReport> validatedReports = trackingRegistry.getValidatorReports();
    System.out.println(TrackingMessageReport.getFormattedReport("Generated Report", generatedReports));
    System.out.println(TrackingMessageReport.getFormattedReport("Validated Report", validatedReports));
  }
}