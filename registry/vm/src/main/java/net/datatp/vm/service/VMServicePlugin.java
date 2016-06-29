package net.datatp.vm.service;

import net.datatp.vm.VMConfig;
import net.datatp.vm.VMDescriptor;


public interface VMServicePlugin {
  public void killVM(VMService vmService, VMDescriptor descriptor) throws Exception ;
  public void shutdownVM(VMService vmService, VMDescriptor descriptor) throws Exception ;
  public void allocateVM(VMService vmService, VMConfig vmConfig) throws Exception ;
  public void shutdown() ;
}
