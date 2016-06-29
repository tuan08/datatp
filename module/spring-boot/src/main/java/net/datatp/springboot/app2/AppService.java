package net.datatp.springboot.app2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

@Service
public class AppService {
  
  @PostConstruct
  public void onInit() throws Exception {
    System.out.println("AppService2: onInit()");
    System.out.println("AppService2: classloader = " + Thread.currentThread().getContextClassLoader().hashCode());
  }
  
  @PreDestroy
  public void onDestroy() throws Exception {
    System.out.println("AppService2: onDestroy()");
  }
}
