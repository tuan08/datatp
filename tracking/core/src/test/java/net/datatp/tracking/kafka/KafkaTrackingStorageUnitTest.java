package net.datatp.tracking.kafka;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.kafka.tool.server.KafkaCluster;
import net.datatp.tracking.TrackingReportApp;
import net.datatp.tracking.kafka.KafkaTrackingApp;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;

public class KafkaTrackingStorageUnitTest {
  final static String WORKING_DIR           = "./build/working";
  final static String TRACKING_PATH         = "/tracking";
  final static int    NUM_OF_CHUNK          = 5;
  final static int    NUM_OF_MESG_PER_CHUNK = 500;
  
  private KafkaCluster             cluster;
  
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("WARN");
    FileUtil.removeIfExist(WORKING_DIR, false);
    
    cluster = new KafkaCluster("./build/kafka", 1, 1);
    cluster.setVerbose(false);
    cluster.setReplication(1);
    cluster.setNumOfPartition(5);
    cluster.start();
    Thread.sleep(2000);
  }
  
  @After
  public void teardown() throws Exception {
    cluster.shutdown();
  }
  
  @Test
  public void testTracking() throws Exception {
    String[] appConfig = {
      "--zk-connect", "localhost:2181",
      
      "--kafka-num-of-partition", "5",
      "--kafka-num-of-replication", "1",
      
      "--tracking-path", "/tracking",
      "--input-topic", "tracking",
      "--output-topic", "tracking",
      "--max-run-time", "10000",
    };
    KafkaTrackingApp app = new KafkaTrackingApp(appConfig);
    app.run();
    
    String[] reportAppConfig = {
      "--zk-connect",    "localhost:2181", 
      "--tracking-path", "/tracking"
    };
    TrackingReportApp.main(reportAppConfig);
  }
}