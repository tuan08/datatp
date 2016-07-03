package net.datatp.activemq;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import net.datatp.springframework.SpringAppLauncher;

@SpringBootApplication
@ComponentScan({ "net.datatp.activemq" })
@EnableConfigurationProperties
public class ActiveMQEmbeddedServer {
  static public void run(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[]{
        "--activemq.data.dir=build/activemq", 
        "--activemq.db.directory=${activemq.data.dir}/data",
        "--activemq.broker.url=vm://localhost"
      };
    }
    String[] config = { "classpath:/META-INF/springframework/activemq-embedded-server.xml" };
    SpringAppLauncher.launch(ActiveMQEmbeddedServer.class, config, args);
  }
  
  public static void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}