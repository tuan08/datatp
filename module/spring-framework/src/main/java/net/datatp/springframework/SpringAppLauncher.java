package net.datatp.springframework;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;


public class SpringAppLauncher {
  
  static public ApplicationContext launch(Class<?> app, String[] configSource, String[] args) throws Exception {
    SpringApplicationBuilder builder = new SpringApplicationBuilder();
    int sourceSize = 1 + configSource.length;
    Object[] sources = new Object[sourceSize];
    sources[0] = app;
    for(int i = 1; i < sources.length; i++) {
      sources[i] = configSource[i -1];
    };
    return builder.sources(sources).run(args);
  }
}