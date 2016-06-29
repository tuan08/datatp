package net.datatp.module;

import java.util.Map;

import net.datatp.module.ModuleConfig;
import net.datatp.module.ServiceModule;
import net.datatp.vm.environment.yarn.AsynYarnManager;
import net.datatp.vm.environment.yarn.SyncYarnManager;
import net.datatp.vm.environment.yarn.YarnManager;
import net.datatp.vm.service.VMServicePlugin;

@ModuleConfig(name = "VMServiceModule", autoInstall = false, autostart = false) 
public class VMServiceModule extends ServiceModule {
  final static public String NAME = "VMServiceModule" ;
  
  @Override
  protected void configure(Map<String, String> properties) {  
    try {
      String vmServicePlugin = properties.get("module.vm.vmservice.plugin");
      if(vmServicePlugin.indexOf("Yarn") >= 0) {
        bindType(YarnManager.class, SyncYarnManager.class);
        //bindType(YarnManager.class, AsynYarnManager.class);
      }
      bindType(VMServicePlugin.class, vmServicePlugin);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}