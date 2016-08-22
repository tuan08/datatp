package net.datatp.springframework;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.loader.PropertiesLauncher;

import net.datatp.util.text.StringUtil;

public class MultipleApplication {
  public static void main(String[] args) throws Exception {
    System.setProperty("loader.main", net.datatp.springframework.app1.Application1.class.getName());
    String[] app1Args = {
      "--spring.cloud.zookeeper.enabled=false",
      "--server.port=-1",
      "--spring.jmx.default-domain=app1"
    };
    PropertiesLauncher.main(StringUtil.merge(app1Args, args));
    
    //System.setProperty("loader.main", Application2.class.getName());
    //PropertiesLauncher.main(new String[] {});
    
    String[] app2Args = {
      "--spring.cloud.zookeeper.enabled=false",
      "--server.port=-1",
      "--spring.jmx.default-domain=app2"
    };
    Map<String, Object> props = new HashMap<>();
    props.put("app.name", "App2");
    props.put("app.servers", "server1,server2");
    SpringApplicationBuilder builder = new SpringApplicationBuilder();
    builder.
      bannerMode(Banner.Mode.OFF).
      properties(props).
      sources(net.datatp.springframework.app2.Application2.class).
      run(StringUtil.merge(app2Args, args));
  }
}