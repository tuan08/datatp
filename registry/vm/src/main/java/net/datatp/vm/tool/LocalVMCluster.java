package net.datatp.vm.tool;

import net.datatp.util.io.FileUtil;
import net.datatp.vm.client.LocalVMClient;
import net.datatp.vm.client.VMClient;
import net.datatp.zookeeper.tool.server.EmbededZKServer;

public class LocalVMCluster {
  private String baseDir = "./build/vm-cluster";
  private EmbededZKServer zookeeperServer ;
  private VMClusterBuilder vmClusterBuilder ;
  
  public LocalVMCluster(String baseDir) throws Exception {
    this.baseDir     = baseDir ;
    vmClusterBuilder = new VMClusterBuilder(null, new LocalVMClient()); 
  }
  
  public String getBaseDir() { return this.baseDir ; }
  
  public VMClient getVMClient() { return vmClusterBuilder.getVMClient() ; }
  
  public VMClusterBuilder getVMClusterBuilder() { return vmClusterBuilder; }
  
  public void clean() throws Exception {
    FileUtil.removeIfExist(baseDir, false);
  }
  
  public void startZookeeper() throws Exception {
    zookeeperServer = new EmbededZKServer(baseDir + "/zookeeper-1", 2181);
    zookeeperServer.start();
  }
  
  public void start() throws Exception {
    this.startZookeeper();
    vmClusterBuilder.start();
  }
  
  public void shutdown() throws Exception {
    vmClusterBuilder.shutdown();
    zookeeperServer.shutdown();
  }
}
