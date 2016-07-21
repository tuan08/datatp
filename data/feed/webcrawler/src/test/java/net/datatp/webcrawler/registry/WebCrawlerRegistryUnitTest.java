package net.datatp.webcrawler.registry;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.datatp.http.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.webcrawler.registry.event.CrawlerEventContext;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.registry.event.Event;
import net.datatp.zk.tool.server.EmbededZKServer;

public class WebCrawlerRegistryUnitTest {
  EmbededZKServer zkServer;
  
  @Before
  public void setup() throws Exception {
    FileUtil.removeIfExist("build/working", false);
    zkServer = new EmbededZKServer("build/working/zookeeper/data", 2182);
    zkServer.start();
  }
  
  @After
  public void teardown() throws Exception {
    zkServer.shutdown();
  }
  
  @Test
  public void test() throws Exception {
    RegistryClient registryClient = new RegistryClient(zkServer.getConnectString());
    
    CrawlerEventContext context = new CrawlerEventContext(null);
    WebCrawlerRegistry wReg = 
        new WebCrawlerRegistry("web-crawler", registryClient).
        listenToSiteConfigEvent(context).
        listenToMasterEvent(context).
        listenToFetcherEvent(context);
    
    wReg.getSiteConfigRegistry().add("group1", newSiteConfig("group1", "vnexpress.net"));
    wReg.getSiteConfigRegistry().add("group1", newSiteConfig("group1", "vietnamenet.vn"));
    wReg.getSiteConfigRegistry().add("group2", newSiteConfig("group2", "ebay.com"));
    registryClient.dump(System.out);
    
    List<String> groups = wReg.getSiteConfigRegistry().getGroups();
    Assert.assertEquals(2, groups.size());
    Assert.assertEquals(2, wReg.getSiteConfigRegistry().getByGroup("group1").size());
    
    wReg.getSiteConfigRegistry().getEventBroadcaster().broadcast(new TestEvent("group1", "vnexpress.net"));
    wReg.getSiteConfigRegistry().getEventBroadcaster().broadcast(new TestEvent("group2", "ebay.com"));
    Thread.sleep(1000);
  }
  
  SiteConfig newSiteConfig(String group, String host) {
    SiteConfig config = new SiteConfig(host);
    config.setGroup(group);
    config.setInjectUrl(new String[] {"http://" + host}) ;
    config.setCrawlDeep(3) ;
    config.setStatus(SiteConfig.Status.Ok) ;
    config.setRefreshPeriod(60 * 60 * 24) ;
    return config;
  }
  
  static public class TestEvent extends Event<CrawlerEventContext> {
    private static final long serialVersionUID = 1L;
    
    private String group;
    private String name ;
    
    public TestEvent() {
    }
    
    public TestEvent(String group, String name) {
      this.group = group;
      this.name  = name;
    }
    
    @Override
    public void execute(RegistryClient registryClient, CrawlerEventContext context) throws Exception {
      System.out.println("Modify: group = " + group);
      System.out.println("Modify: name  = " + name);
    }
  }
}
