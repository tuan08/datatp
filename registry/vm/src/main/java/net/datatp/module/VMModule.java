package net.datatp.module;

import java.util.Map;

import net.datatp.module.ModuleConfig;
import net.datatp.module.ServiceModule;



@ModuleConfig(name = "VMModule", autoInstall = false, autostart = false) 
public class VMModule extends ServiceModule {
  final static public String NAME = "VMModule" ;
  
  @Override
  protected void configure(Map<String, String> properties) {  
  }
}