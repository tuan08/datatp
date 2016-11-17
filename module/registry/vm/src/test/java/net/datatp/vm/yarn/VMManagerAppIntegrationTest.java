package net.datatp.vm.yarn;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.JCommander;

import net.datatp.vm.HadoopProperties;
import net.datatp.vm.VMConfig;
import net.datatp.vm.VMDescriptor;
import net.datatp.vm.VMDummyApp;
import net.datatp.vm.client.VMClient;
import net.datatp.vm.client.YarnVMClient;
import net.datatp.vm.client.shell.Shell;
import net.datatp.vm.command.CommandResult;
import net.datatp.vm.command.VMCommand;
import net.datatp.vm.environment.yarn.AppClient;
import net.datatp.vm.environment.yarn.MiniClusterUtil;
import net.datatp.vm.service.VMServiceApp;
import net.datatp.vm.service.VMServiceCommand;
import net.datatp.zk.tool.server.EmbededZKServer;
import net.datatp.registry.Registry;
import net.datatp.registry.RegistryConfig;
import net.datatp.registry.zk.RegistryImpl;

public class VMManagerAppIntegrationTest {
  
  static {
    System.setProperty("java.net.preferIPv4Stack", "true") ;
    System.setProperty("log4j.configuration", "file:src/test/resources/test-log4j.properties") ;
  }
 
  private EmbededZKServer zookeeperServer ;
  MiniYARNCluster miniYarnCluster ;
  YarnVMClient vmClient;

  @Before
  public void setup() throws Exception {
    YarnConfiguration yarnConf = new YarnConfiguration() ;
    yarnConf.set("io.serializations", "org.apache.hadoop.io.serializer.JavaSerialization");
    miniYarnCluster = MiniClusterUtil.createMiniYARNCluster(yarnConf, 1);
    
    HadoopProperties yarnProps = new HadoopProperties();
    yarnProps.put("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030");
    
    zookeeperServer = new EmbededZKServer("build/zookeeper-1", 2181);
    zookeeperServer.clean();
    zookeeperServer.start();
    
    Registry registry = new RegistryImpl(RegistryConfig.getDefault());
    registry.connect();
    vmClient = new YarnVMClient(registry, yarnProps,miniYarnCluster.getConfig());
  }

  @After
  public void teardown() throws Exception {
    zookeeperServer.shutdown();
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }

  @Test
  public void testAppClient() throws Exception {
    Shell shell = new Shell(vmClient) ;
    
    String[] args = createVMConfigArgs("vm-master-1");
    VMConfig vmConfig = new VMConfig() ;
    new JCommander(vmConfig, args) ;
    AppClient appClient = new AppClient(vmConfig.getHadoopProperties()) ;
    appClient.run(vmConfig, new YarnConfiguration(miniYarnCluster.getConfig()));
    Thread.sleep(10000);
    
    shell.execute("vm info");
    VMDescriptor vmMaster1 = shell.getVMClient().getMasterVMDescriptor();
    
    VMDescriptor vmDummy1 = allocateVMDummy(vmClient, "vm-dummy-1") ;
    shell.execute("vm info");
    Thread.sleep(5000);

    VMDescriptor vmDummy2 = allocateVMDummy(vmClient, "vm-dummy-2") ;
    shell.execute("vm info");
    Thread.sleep(5000);
    
    shutdown(vmClient, vmDummy1);
    shutdown(vmClient, vmDummy2);
    Thread.sleep(1000);
    shell.execute("vm info");
    shell.execute("registry dump");
    shutdown(vmClient, vmMaster1);
    Thread.sleep(1000);
    shell.execute("vm info");
  }
  
  private String[] createVMConfigArgs(String name) {
    String[] args = { 
        "--environment", "YARN_MINICLUSTER",
        "--name", name,
        "--role", "vm-master",
        "--self-registration",
        "--registry-connect", "127.0.0.1:2181", 
        "--registry-db-domain", "/NeverwinterDP", 
        "--registry-implementation", RegistryImpl.class.getName(),
        "--vm-application",VMServiceApp.class.getName(),
        "--hadoop:yarn.resourcemanager.scheduler.address=0.0.0.0:8030"
    } ;
    return args;
  }
  
  private VMDescriptor allocateVMDummy(VMClient vmClient, String name) throws Exception {
    String[] args = { 
        "--environment", "YARN_MINICLUSTER",
        "--name", name,
        "--role", "dummy",
        "--self-registration",
        "--registry-connect", "127.0.0.1:2181", 
        "--registry-db-domain", "/NeverwinterDP", 
        "--registry-implementation", RegistryImpl.class.getName(),
        "--vm-application", VMDummyApp.class.getName(),
        "--hadoop:yarn.resourcemanager.scheduler.address=0.0.0.0:8030"
    } ;
    VMDescriptor masterVMDescriptor = vmClient.getMasterVMDescriptor();
    VMConfig vmConfig = new VMConfig() ;
    new JCommander(vmConfig, args);
    CommandResult<?> result = vmClient.execute(masterVMDescriptor, new VMServiceCommand.Allocate(vmConfig));
    Assert.assertNull(result.getErrorStacktrace());
    VMDescriptor vmDescriptor = result.getResultAs(VMDescriptor.class);
    Assert.assertNotNull(vmDescriptor);
    return vmDescriptor;
  }
  
  private boolean shutdown(VMClient vmClient, VMDescriptor vmDescriptor) throws Exception {
    CommandResult<?> result = vmClient.execute(vmDescriptor, new VMCommand.Shutdown());
    Assert.assertNull(result.getErrorStacktrace());
    return result.getResultAs(Boolean.class);
  }
}