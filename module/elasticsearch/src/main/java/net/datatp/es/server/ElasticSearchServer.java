package net.datatp.es.server;

import java.io.IOException;

import org.slf4j.Logger;

//import net.datatp.module.AppServiceModule;
//import net.datatp.module.MycilaJmxModuleExt;
import net.datatp.tool.server.Server;

public class ElasticSearchServer implements Server {
  private String nodeName ;
  private String hostname = "localhost";
  private int    port     = 9300 ;
  private String dataDir  = null ;
  private ElasticSearchServerThread elasticServiceThread;
  
  public ElasticSearchServer(String nodeName, String hostname, int port, String dataDir) {
    this.nodeName = nodeName; 
    this.hostname = hostname;
    this.port = port;
    this.dataDir = dataDir ;
  }
  
  @Override
  public String getHost() { return hostname; }

  @Override
  public int getPort() { return port; }

  @Override
  public String getConnectString() { return hostname + ":" + port; }

  public Logger getLogger() {
    if(elasticServiceThread == null) return null ;
    return elasticServiceThread.elasticService.getLogger();
  }
  
  @Override
  public void start() throws Exception {
    elasticServiceThread = new ElasticSearchServerThread() ;
    elasticServiceThread.start();
  }

  @Override
  public void shutdown() {
    if(elasticServiceThread != null) {
      try {
        elasticServiceThread.elasticService.stop();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public class ElasticSearchServerThread extends Thread {
    private ElasticSearchService elasticService ;
    
    public void run() {
    }
  }
}
