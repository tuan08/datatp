package net.datatp.springframework.app2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
  @Autowired
  private AppConfig config;
  
  @PostConstruct
  public void onInit() throws Exception {
    System.out.println("app2.AppService: onInit()");
    System.out.println("app2.AppService: classloader = " + Thread.currentThread().getContextClassLoader().hashCode());
    System.out.println("app2.AppService: app.name = " + config.getName());
  }
  
  @PreDestroy
  public void onDestroy() throws Exception {
    System.out.println("app2.AppService: onDestroy()");
  }
}
