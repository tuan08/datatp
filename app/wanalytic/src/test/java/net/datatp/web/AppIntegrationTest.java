package net.datatp.web;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.util.io.IOUtil;
import net.datatp.wanalytic.LocalCluster;
import net.datatp.wanalytic.facebook.FBCrawler;
import net.datatp.wanalytic.facebook.FBObjectIdQueue;
import net.datatp.wanalytic.flink.WAnalytic;

public class AppIntegrationTest {
  private LocalCluster localCluster ;
  
  @Before
  public void setup() throws Exception {
    localCluster = new LocalCluster("build/cluster");
    localCluster.useLog4jConsoleOutput("WARN");
    localCluster.clean();
    localCluster.start();
  }
  
  @After
  public void teardown() throws Exception {
    localCluster.shutdown();
  }
  
  @Test
  public void testApp() throws Exception {
    String accountIdText = IOUtil.loadResAsString("classpath:facebook/user-ids.txt");
    String[] accountIds = accountIdText.split("\n");
    FBObjectIdQueue  objectIdQueue = new FBObjectIdQueue(Arrays.asList(accountIds));
    FBCrawler crawler = new FBCrawler(localCluster.getKafkaCluster().getZKConnect(), objectIdQueue, 3);
    crawler.start();
    Thread.sleep(3000);
    
    WAnalytic wanalytic = new WAnalytic();
    wanalytic.runLocal();
    Thread.sleep(1000000000);
  }
}
