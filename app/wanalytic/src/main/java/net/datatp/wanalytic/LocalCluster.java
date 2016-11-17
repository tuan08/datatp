package net.datatp.wanalytic;

import java.util.Properties;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import net.datatp.kafka.tool.server.KafkaCluster;
import net.datatp.util.io.FileUtil;
import net.datatp.util.io.IOUtil;
import net.datatp.util.log.LoggerFactory;

public class LocalCluster {
  private String baseDir ;
  private KafkaCluster kafkaCluster;
  private Node esNode ;
  
  public LocalCluster(String baseDir) throws Exception {
    this.baseDir = baseDir;
    kafkaCluster = new KafkaCluster(baseDir, 1/*numOfZkInstances*/, 1/*numOfKafkaInstances*/);
    kafkaCluster.setNumOfPartition(5);
  }
  
  public KafkaCluster getKafkaCluster() { return this.kafkaCluster; }

  
  public void clean() throws Exception {
    FileUtil.removeIfExist(baseDir, false);
  }
  
  public void useLog4jConfig(String resPath) throws Exception {
    Properties log4jProps = new Properties() ;
    log4jProps.load(IOUtil.loadRes(resPath));
    log4jProps.setProperty("log4j.rootLogger", "INFO, file");
    LoggerFactory.log4jConfigure(log4jProps);
  }
  
  public void useLog4jConsoleOutput(String level) throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig(level);
  }
  
  public void start() throws Exception {
    h1("Start Elasticsearch");
    
    Settings.Builder settingBuilder = Settings.builder();
    settingBuilder.put("cluster.name",       "elasticsearch");
    settingBuilder.put("path.home",          baseDir + "/elasticsearch/data");
    esNode = new Node(settingBuilder.build());
    
    h1("Start kafka cluster");
    kafkaCluster.start();
    Thread.sleep(1000);
  }
  
  public void shutdown() throws Exception {
    kafkaCluster.shutdown();
    esNode.close();
  }
  
  
  static public void h1(String title) {
    System.out.println("\n\n");
    System.out.println("------------------------------------------------------------------------");
    System.out.println(title);
    System.out.println("------------------------------------------------------------------------");
  }
  
  static public void h2(String title) {
    System.out.println(title);
    StringBuilder b = new StringBuilder() ; 
    for(int i = 0; i < title.length(); i++) {
      b.append("-");
    }
    System.out.println(b) ;
  }
  
  static public String format(String tmpl, Object ... args) {
    return String.format(tmpl, args) ;
  }
}