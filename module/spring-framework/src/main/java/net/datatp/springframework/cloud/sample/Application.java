package net.datatp.springframework.cloud.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import net.datatp.util.io.FileUtil;
import net.datatp.zk.tool.server.EmbededZKServer;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan({"net.data.springframework.cloud.sample"})
public class Application {
  public static void main(String[] args) throws Exception {
    FileUtil.removeIfExist("build/zookeeper/data", false);
    EmbededZKServer zkServer = new EmbededZKServer("build/zookeeper/data", 2182);
    zkServer.start();
    
    SpringApplication.run(Application.class, args);
  }
}