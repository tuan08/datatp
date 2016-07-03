package net.datatp.springframework.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "net.datatp.springframework.app1" })
//@ImportResource("classpath:META-INF/application1-config.xml")
public class Application1 {
  public static void main(String[] args) {
    Object[] sources = { Application1.class, "classpath:META-INF/application1-config.xml"};
    ConfigurableApplicationContext context = SpringApplication.run(sources, args);
  }
}