package net.datatp.springframework.app2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( prefix="app" )
public class AppConfig {
  
  private String name ;
  private List<String> servers = new ArrayList<String>();
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public List<String> getServers() { return servers; }
  public void setServers(List<String> servers) { this.servers = servers; }
}
