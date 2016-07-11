package net.datatp.tracking.es;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.tracking.TrackingReportApp;
import net.datatp.tracking.es.ESTrackingApp;
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

    NodeBuilder nb = nodeBuilder();
    nb.getSettings().put("cluster.name",       "neverwinterdp");
    nb.getSettings().put("path.home",          WORKING_DIR + "/elasticsearch/data");
    nb.getSettings().put("node.name",          "localhost");
    nb.getSettings().put("transport.tcp.port", "9300");

    node = nb.node();
  }
  
  @After
  public void after() {
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
