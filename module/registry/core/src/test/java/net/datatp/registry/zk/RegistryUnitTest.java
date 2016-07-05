package net.datatp.registry.zk;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.datatp.util.io.FileUtil;
import net.datatp.zookeeper.tool.server.EmbededZKServer;
import net.datattp.registry.MultiDataGet;
import net.datattp.registry.Node;
import net.datattp.registry.NodeCreateMode;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.event.NodeEvent;
import net.datattp.registry.event.NodeWatcher;
import net.datattp.registry.zk.RegistryImpl;

public class RegistryUnitTest {
  static {
    System.setProperty("log4j.configuration", "file:src/test/resources/test-log4j.properties") ;
  }
  
  private EmbededZKServer zkServerLauncher ;
  
  @Before
  public void setup() throws Exception {
    FileUtil.removeIfExist("./build/data", false);
    
    zkServerLauncher = new EmbededZKServer("./build/data/zookeeper") ;
    zkServerLauncher.start();
  }
  
  @After
  public void teardown() throws Exception {
    zkServerLauncher.shutdown();
  }

  @Test
  public void testPersistent() throws Exception {
    String DATA = "hello";
    
    Registry registry = newRegistry().connect(); 
    Node pNode = registry.create("/persistent", DATA.getBytes(), NodeCreateMode.PERSISTENT) ;
    Assert.assertEquals("/persistent", pNode.getPath()) ;
    Assert.assertTrue(pNode.exists());
    Assert.assertEquals(DATA, new String(pNode.getData())) ;
    registry.shutdown();
    
    registry = newRegistry().connect();
    pNode = registry.get("/persistent") ;
    Assert.assertTrue(pNode.exists());
    //TODO: should test the the create child , create event , modify data event
    NodeEventCatcher pNodeEventCatcher = new NodeEventCatcher() ;
    pNode.watch(pNodeEventCatcher) ;
    Assert.assertEquals(DATA, new String(pNode.getData())) ;
    pNode.delete();
    Assert.assertFalse(pNode.exists());
    Thread.sleep(1000); //wait to make sure that the watcher is invoked
    Assert.assertEquals(pNode.getPath(), pNodeEventCatcher.getNodeEvent().getPath());
    Assert.assertEquals(NodeEvent.Type.DELETE, pNodeEventCatcher.getNodeEvent().getType());
    registry.shutdown();
  }
  
  @Test
  public void testPersistentSequential() throws Exception {
    Registry  registry = newRegistry().connect(); 
    
    registry.create("/sequential", NodeCreateMode.PERSISTENT) ;
    Node seqNode1 = registry.create("/sequential/node-", NodeCreateMode.PERSISTENT_SEQUENTIAL) ;
    seqNode1.createChild("report", registry.getRegistryConfig(), NodeCreateMode.PERSISTENT);
    Assert.assertTrue(seqNode1.getPath().matches("/sequential/node-0+")) ;
    registry.shutdown();
  }
  
  @Test
  public void testEphemeral() throws Exception {
    Registry registry = newRegistry().connect(); 
    registry.create("/ephemeral", NodeCreateMode.PERSISTENT) ;
    Node ephemeralNode = registry.create("/ephemeral/node", NodeCreateMode.EPHEMERAL) ;
    Assert.assertEquals("/ephemeral/node", ephemeralNode.getPath()) ;
    Assert.assertTrue(ephemeralNode.exists()) ;
    registry.shutdown();
    
    registry = newRegistry().connect();
    Assert.assertTrue(registry.get("/ephemeral").exists());
    Assert.assertFalse(registry.get(ephemeralNode.getPath()).exists());
    registry.shutdown();
  }
  
  @Test
  public void testEphemeralSequential() throws Exception {
    Registry registry = newRegistry().connect(); 
    registry.create("/ephemeral-sequential", NodeCreateMode.PERSISTENT) ;
    
    Node seqNode = registry.create("/ephemeral-sequential/node", NodeCreateMode.EPHEMERAL_SEQUENTIAL) ;
    Assert.assertTrue(seqNode.getPath().matches("/ephemeral-sequential/node0+")) ;
    Assert.assertTrue(seqNode.exists()) ;
    registry.shutdown();
    
    registry = newRegistry().connect();
    Assert.assertFalse(registry.get(seqNode.getPath()).exists());
    registry.shutdown();
  }
  
  @Test
  public void testExistsWatcher() throws Exception {
    String watchPath = "/node/watch" ;
    final CountDownLatch existsSignal = new CountDownLatch(1);
    Registry registry = newRegistry().connect(); 
    registry.watchExists(watchPath, new NodeWatcher() {
      @Override
      public void onEvent(NodeEvent event) {
        existsSignal.countDown();
      }
    });
    registry.createIfNotExist(watchPath);
    existsSignal.await(1, TimeUnit.SECONDS);
    Assert.assertEquals(0,existsSignal.getCount());
    registry.shutdown();
  }
  
  
  @Test
  public void testModifyWatcher() throws Exception {
    final String path = "/node/exists" ;
    final CountDownLatch modifySignal = new CountDownLatch(2);
    final Registry registry = newRegistry().connect(); 
    registry.createIfNotExist(path);
    NodeWatcher watcher = new NodeWatcher() {
      @Override
      public void onEvent(NodeEvent event) {
        modifySignal.countDown();
        System.out.println("got event.....");
      }
    };
    registry.watchModify(path, watcher);
    registry.setData(path, new byte[10]);
    //the watcher suppose to trigger once
    registry.setData(path, new byte[10]);
    modifySignal.await(500, TimeUnit.MILLISECONDS);
    Assert.assertEquals(1, modifySignal.getCount());
    
    registry.shutdown();
  }
  
  @Test
  public void test_rcopy() throws Exception {
    final Registry registry = newRegistry().connect(); 
    String path = "/from" ;
    registry.create(path, path.getBytes(), NodeCreateMode.PERSISTENT);
    for(int i = 0; i < 3; i++) {
      path = path + "/" + i ;
      registry.create(path, path.getBytes(), NodeCreateMode.PERSISTENT);
    }
    registry.rcopy("/from", "/to");
    registry.get("/").dump(System.out);
    registry.shutdown();
  }
  
  @Test
  public void testMultiDataGet() throws Exception {
    final Registry registry = newRegistry().connect();
    int NUM_OF_NODE = 100 ;
    Node dataNode = registry.create("/data", NodeCreateMode.PERSISTENT);
    String desc = "This is a very long .......................................... description " ;
    for(int i = 0; i < NUM_OF_NODE; i++) {
      dataNode.createChild("node-" + i, new HelloData(desc + i), NodeCreateMode.PERSISTENT);
    }
    MultiDataGet<HelloData> multiDataGet = registry.createMultiDataGet(HelloData.class) ;
    for(int i = 0; i < NUM_OF_NODE; i++) {
      multiDataGet.get("/data/node-" + i);
    }
    multiDataGet.waitForAllGet(5000);
    Assert.assertEquals(NUM_OF_NODE, multiDataGet.getProcessResultCount());
    Assert.assertEquals(0, multiDataGet.getProcessErrorGetCount());
    List<HelloData> holder = multiDataGet.getResults();
    for(int i = 0; i < holder.size(); i++) {
      HelloData sel = holder.get(i);
      System.out.println(sel.getDescription());
    }
    registry.shutdown();
  }
  
  private Registry newRegistry() {
    return new RegistryImpl(RegistryConfig.getDefault()) ;
  }
  
  public class NodeEventCatcher extends NodeWatcher {
    private NodeEvent nodeEvent ;
    
    public void onEvent(NodeEvent event) {
      this.nodeEvent = event ;
    }
    
    public NodeEvent getNodeEvent() { return this.nodeEvent ; }
  }
  
  static public class HelloData {
    String description ;

    public HelloData() {}
    
    public HelloData(String desc) {
      this.description = desc; 
    }
    
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
  }
}
