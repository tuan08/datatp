package net.datatp.es.log4j;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Before;
import org.junit.Test;

import net.datatp.es.log4j.ElasticSearchAppender;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
/**
 * $Author: Tuan Nguyen$
 **/
public class ESAppenderUnitTest {
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
  }
  
  @Test
  public void test() throws Exception {
    ElasticSearchAppender appender = new ElasticSearchAppender();
    appender.init(new String[] { "127.0.0.1:9300" }, "log4j", "build/buffer/es/log4j");
    appender.activateOptions();
    Thread.sleep(5000);
    
    FileUtil.removeIfExist("build/elasticsearch", false);
    FileUtil.removeIfExist("build/buffer", false);
    NodeBuilder nb = nodeBuilder();
    nb.getSettings().put("cluster.name",       "neverwinterdp");
    nb.getSettings().put("path.home",          "build/elasticsearch/data");
    nb.getSettings().put("node.name",          "elasticsearch-1");
    nb.getSettings().put("transport.tcp.port", "9300");
    Node node = nb.node();
    
    Thread.sleep(10000);
    appender.close();
    node.close();
  }
}
