package net.datatp.module;

import java.util.Map;

import net.datatp.es.log.MetricLoggerService;
import net.datatp.es.sys.SysInfoLoggerService;
import net.datatp.module.ModuleConfig;
import net.datatp.module.ServiceModule;

@ModuleConfig(name = "ESOSMonitorLoggerModule", autoInstall = false, autostart = false) 
public class ESOSMonitorLoggerModule extends ServiceModule {
  final static public String NAME = "ESOSMonitorLoggerModule";
  
  @Override
  protected void configure(Map<String, String> properties) {  
    bind(SysInfoLoggerService.class).asEagerSingleton();
    bind(MetricLoggerService.class).asEagerSingleton();
  }
}