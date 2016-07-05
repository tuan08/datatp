package net.datatp.vm.client;

import net.datatp.vm.VM;
import net.datatp.vm.VMConfig;
import net.datatp.vm.environment.jvm.JVMVMServicePlugin;
import net.datatp.vm.service.VMServiceApp;
import net.datatp.vm.service.VMServicePlugin;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.RegistryException;
import net.datattp.registry.zk.RegistryImpl;

public class LocalVMClient extends VMClient {
  public LocalVMClient() throws RegistryException {
    this(new RegistryImpl(RegistryConfig.getDefault()));
  }
  
  public LocalVMClient(Registry registry) {
    super(registry);
  }
  
  @Override
  public void createVMMaster(String localAppHome, String name) throws Exception {
    VMConfig vmConfig = new VMConfig() ;
    vmConfig.
      setVmId(name).
      addRoles("vm-master").
      setSelfRegistration(true).
      setVmApplication(VMServiceApp.class.getName()).
      addProperty("implementation:" + VMServicePlugin.class.getName(), JVMVMServicePlugin.class.getName()).
      setRegistryConfig(getRegistry().getRegistryConfig());
    configureEnvironment(vmConfig);
    VM vm = VM.run(vmConfig);
  }
  
  public void configureEnvironment(VMConfig vmConfig) {
    vmConfig.setClusterEnvironment(VMConfig.ClusterEnvironment.JVM);
  }

}
