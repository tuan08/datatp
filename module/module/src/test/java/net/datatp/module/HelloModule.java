package net.datatp.module;

import java.util.Map;

import net.datatp.module.ModuleConfig;
import net.datatp.module.ServiceModule;



@ModuleConfig(name = "HelloModule", autoInstall = false, autostart = false) 
public class HelloModule extends ServiceModule {
  @Override
  protected void configure(Map<String, String> properties) {  
    bind(Hello.class) ;
  }
}