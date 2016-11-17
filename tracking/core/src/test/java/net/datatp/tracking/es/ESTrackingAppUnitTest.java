package net.datatp.tracking.es;

import java.io.IOException;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.tracking.TrackingReportApp;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
import net.datatp.zk.tool.server.EmbededZKServer;


public class ESTrackingAppUnitTest {
  static String WORKING_DIR = "build/working";
  
  private Node node ;
  private EmbededZKServer zkServer ;
  
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    FileUtil.removeIfExist(WORKING_DIR, false);
    
    zkServer = new EmbededZKServer(WORKING_DIR + "/zookeeper") ;
    zkServer.start();

    Settings.Builder settingBuilder = Settings.builder();
    settingBuilder.put("cluster.name",       "elasticsearch");
    settingBuilder.put("path.home",          "build/elasticsearch/data");
    settingBuilder.put("node.name",          "localhost");
    settingBuilder.put("transport.tcp.port", "9300");
    node = new Node(settingBuilder.build());
  }
  
  @After
  public void after() throws IOException {
    zkServer.shutdown();
    node.close();
  }
  
  @Test
  public void testService() throws Exception {
    String[] appConfig = {
        "--zk-connect",    "localhost:2181",
        "--es-connect",    "localhost:9300",
        "--tracking-path", "/tracking",
        "--num-of-chunk",  "10",
        "--num-of-message-per-chunk", "1000",
        "--max-run-time",  "60000",
      };
      ESTrackingApp app = new ESTrackingApp(appConfig);
      app.run();
      
      String[] reportAppConfig = {
        "--zk-connect",    "localhost:2181", 
        "--tracking-path", "/tracking"
      };
      TrackingReportApp.main(reportAppConfig);
  }
}
