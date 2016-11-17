package net.datatp.es.log4j;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.Test;

import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
/**
 * $Author: Tuan Nguyen$
 **/
public class ESAppenderUnitTest {
//  @Before
//  public void setup() throws Exception {
//    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
//  }
//  
//  @Test
//  public void test() throws Exception {
//    ElasticSearchAppender appender = new ElasticSearchAppender();
//    appender.init(new String[] { "127.0.0.1:9300" }, "log4j", "build/buffer/es/log4j");
//    appender.activateOptions();
//    Thread.sleep(5000);
//    
//    FileUtil.removeIfExist("build/elasticsearch", false);
//    FileUtil.removeIfExist("build/buffer", false);
//    
//    Settings.Builder settingBuilder = Settings.builder();
//    settingBuilder.put("cluster.name",       "elasticsearch");
//    settingBuilder.put("path.home",          "build/elasticsearch/data");
//    settingBuilder.put("node.name",          "localhost");
//    settingBuilder.put("transport.tcp.port", "9300");
//    Node node = new Node(settingBuilder.build());
//    
//    Thread.sleep(10000);
//    appender.close();
//    node.close();
//  }
}
