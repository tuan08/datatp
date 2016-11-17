package net.datatp.vm.environment.jvm;

import com.google.inject.Singleton;
import com.mycila.jmx.annotation.JmxBean;
import com.mycila.jmx.annotation.JmxField;

import net.datatp.vm.VM;
import net.datatp.vm.VMConfig;
import net.datatp.vm.VMDescriptor;
import net.datatp.vm.VMApp.TerminateEvent;
import net.datatp.vm.service.VMService;
import net.datatp.vm.service.VMServicePlugin;
import net.datatp.registry.RegistryException;

@Singleton
@JmxBean("role=vm-manager, type=VMServicePlugin, name=JVMVMServicePlugin")
public class JVMVMServicePlugin implements VMServicePlugin {
  @JmxField
  private int allocateCount = 0;
  
  @JmxField
  private int killCount = 0;
  
  @JmxField
  private int shutdownCount = 0;

  
  @Override
  synchronized public void allocateVM(VMService vmService, VMConfig vmConfig) throws RegistryException, Exception {
    VM vm = new VM(vmConfig);
    vm.run();
    VM.trackVM(vm);
    allocateCount++ ;
  }

  @Override
  synchronized public void killVM(VMService vmService, VMDescriptor vmDescriptor) throws Exception {
    VM found = VM.getVM(vmDescriptor);
    if(found == null) return;
    found.terminate(TerminateEvent.SimulateKill, 1000);
    killCount++ ;
  }

  @Override
  synchronized public void shutdownVM(VMService vmService, VMDescriptor vmDescriptor) throws Exception {
    VM found = VM.getVM(vmDescriptor);
    if(found == null) return;
    found.terminate(TerminateEvent.Shutdown, 1000);
    shutdownCount++ ;
  }

  @Override
  public void shutdown() {
  }
}