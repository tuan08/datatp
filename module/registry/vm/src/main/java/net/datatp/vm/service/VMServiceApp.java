package net.datatp.vm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.module.AppContainer;
import net.datatp.module.ServiceModuleContainer;
import net.datatp.module.VMServiceModule;
import net.datatp.vm.VMApp;
import net.datatp.vm.VMConfig;
import net.datatp.vm.VMDescriptor;
import net.datatp.vm.VMConfig.ClusterEnvironment;
import net.datatp.vm.environment.jvm.JVMVMServicePlugin;
import net.datatp.vm.environment.yarn.YarnVMServicePlugin;
import net.datatp.vm.event.VMShutdownEventListener;
import net.datatp.registry.RefNode;
import net.datatp.registry.Registry;
import net.datatp.registry.election.LeaderElection;
import net.datatp.registry.election.LeaderElectionListener;


public class VMServiceApp extends VMApp {
  private LeaderElection election ;
  
  private ServiceModuleContainer vmServiceModuleContainer;
  private VMShutdownEventListener shutdownListener;
  
  public VMService getVMService() { return vmServiceModuleContainer.getInstance(VMService.class); }
 
  @Override
  public void run() throws Exception {
    election = new LeaderElection(getVM().getVMRegistry().getRegistry(), VMService.MASTER_LEADER_PATH) ;
    election.setListener(new VMServiceLeaderElectionListener());
    election.start();
    
    Registry registry = getVM().getVMRegistry().getRegistry();
    
    shutdownListener = new VMShutdownEventListener(registry) {
      @Override
      public void onShutdownEvent() throws Exception {
        terminate(TerminateEvent.Shutdown);
      }
    };

    try {
      getVM().getLogger().info("Wait for terminate.....");
      waitForTerminate();
    } catch(InterruptedException ex) {
    } finally {
      getVM().getLogger().info("Terminate, start cleaning........");
      if(election != null && election.getLeaderId() != null) {
        election.stop();
      }
      VMService vmService = getVMService();
      if(vmService != null) {
        //TODO: should check to make sure the resource are clean before destroy the service
        Thread.sleep(3000);
        vmService.shutdown();
      }
    }
  }
  
  public void startVMService() {
    try {
      VMConfig vmConfig = getVM().getDescriptor().getVmConfig();
      final Registry registry = getVM().getVMRegistry().getRegistry();
      RefNode refNode = new RefNode();
      refNode.setPath(getVM().getDescriptor().getRegistryPath());
      registry.setData(VMService.MASTER_LEADER_PATH, refNode);
      AppContainer appContainer = getVM().getAppContainer();
      Map<String, String> moduleProps = new HashMap<>() ;
      if(vmConfig.getClusterEnvironment() ==  ClusterEnvironment.JVM) {
        moduleProps.put("module.vm.vmservice.plugin", JVMVMServicePlugin.class.getName());
      } else {
        moduleProps.put("module.vm.vmservice.plugin", YarnVMServicePlugin.class.getName());
      }
      appContainer.install(moduleProps, VMServiceModule.NAME);
      vmServiceModuleContainer = appContainer.getModule(VMServiceModule.NAME);
     
      VMService vmService = vmServiceModuleContainer.getInstance(VMService.class);
      vmService.setStatus(VMService.Status.RUNNING);
      List<VMDescriptor> vmDescriptors = vmService.getActiveVMDescriptors();
      for(VMDescriptor sel : vmDescriptors) {
        if(vmService.isRunning(sel)) vmService.watch(sel);
        else vmService.unregister(sel);
      }
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }
  
  class VMServiceLeaderElectionListener implements LeaderElectionListener {
    @Override
    public void onElected() {
      startVMService();
    }
  }
  
 
}