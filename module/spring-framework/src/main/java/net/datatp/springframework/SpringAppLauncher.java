package net.datatp.springframework;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


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
  
  static public ApplicationContext launch(String[] config, String ... args) throws Exception {
    SpringApplicationBuilder builder = new SpringApplicationBuilder();
    Object[] sources = new Object[config.length];
    for(int i = 0; i < sources.length; i++) {
      sources[i] = config[i];
    };
    return builder.sources(sources).run(args);
  }
  
  static public void launch(String[] config) throws Exception {
    final GenericApplicationContext ctx = new GenericApplicationContext();
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
    xmlReader.loadBeanDefinitions(config);
    ctx.refresh();
    ctx.registerShutdownHook();
  }
}