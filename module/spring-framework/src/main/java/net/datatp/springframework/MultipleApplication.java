package net.datatp.springframework;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.loader.PropertiesLauncher;

public class MultipleApplication {
  public static void main(String[] args) throws Exception {
    System.setProperty("loader.main", net.datatp.springframework.app1.Application1.class.getName());
    PropertiesLauncher.main(new String[] {});
    
    //System.setProperty("loader.main", Application.class.getName());
    //PropertiesLauncher.main(new String[] {});
    
    Map<String, Object> props = new HashMap<>();
    props.put("app.name", "App2");
    props.put("app.servers", "server1,server2");
    SpringApplicationBuilder builder = new SpringApplicationBuilder();
    builder.
      bannerMode(Banner.Mode.OFF).
      properties(props).
      sources(net.datatp.springframework.app2.Application.class).
      run(args);
  }
}
