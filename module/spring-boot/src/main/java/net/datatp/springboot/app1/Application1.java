package net.datatp.springboot.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "net.datatp.springboot.app1" })
public class Application1 {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application1.class, args);
  }
}
