package net.datatp.springboot;

import org.springframework.boot.loader.PropertiesLauncher;

import net.datatp.springboot.app1.Application1;
import net.datatp.springboot.app2.Application2;

public class MultipleApplication {
  public static void main(String[] args) throws Exception {
    System.setProperty("loader.main", Application1.class.getName());
    PropertiesLauncher.main(new String[] {});
    System.setProperty("loader.main", Application2.class.getName());
    PropertiesLauncher.main(new String[] {});
  }
}
