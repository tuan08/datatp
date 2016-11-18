package net.datatp.springframework.app2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import net.datatp.util.text.StringUtil;

@SpringBootApplication
@ComponentScan({ "net.datatp.springframework.app2" })
@EnableConfigurationProperties
public class Application2 {
  public static void main(String[] args) {
    String[] appArgs = {
      "--spring.cloud.zookeeper.enabled=false",
      "--server.port=-1",
    };
    if(args != null) appArgs = StringUtil.merge(appArgs, args);
    ConfigurableApplicationContext context = SpringApplication.run(Application2.class, appArgs);
  }
}