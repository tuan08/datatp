package net.datatp.activemq;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import net.datatp.springframework.SpringAppLauncher;
import net.datatp.util.text.StringUtil;

@SpringBootApplication
@Configuration
@PropertySources(value = {
    @PropertySource("classpath:embedded-activemq.properties")
  }
)  
@ComponentScan({ "net.datatp.activemq" })
@EnableConfigurationProperties
@EnableAutoConfiguration
@ConditionalOnProperty(value = "activemq.embedded.enabled", matchIfMissing = false)
public class EmbeddedActiveMQServer {
  static public void run(String[] args) throws Exception {
    String[] defaultArgs = {
      "--server.port=-1",
      "--activemq.embedded.enabled=true"
    };
    String[] config = {  };
    SpringAppLauncher.launch(EmbeddedActiveMQServer.class, config, StringUtil.merge(defaultArgs, args));
  }
  
  public static void main(String[] args) throws Exception {
    run(args);
    Thread.currentThread().join();;
  }
}