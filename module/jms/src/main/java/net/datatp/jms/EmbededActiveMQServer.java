package net.datatp.jms;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Author : Tuan Nguyen tuan.nguyen@headvances.com Apr 21, 2010
 */
public class EmbededActiveMQServer {
  static public void run() throws Exception {
    final GenericApplicationContext ctx = new GenericApplicationContext();
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
    String[] res = { "classpath:/META-INF/file-store-activemq.xml", };
    xmlReader.loadBeanDefinitions(res);
    ctx.refresh();
    ctx.registerShutdownHook();
  }

  static public void main(String[] args) throws Exception {
    run();
    Thread.currentThread().join();
  }
}