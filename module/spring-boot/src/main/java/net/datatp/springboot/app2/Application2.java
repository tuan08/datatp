package net.datatp.springboot.app2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "net.datatp.springboot.app2" })
public class Application2 {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application2.class, args);
  }
}
