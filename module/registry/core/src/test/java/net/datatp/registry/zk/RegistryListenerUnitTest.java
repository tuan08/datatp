package net.datatp.registry.zk;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.datatp.util.io.FileUtil;
import net.datatp.zookeeper.tool.server.EmbededZKServer;
import net.datattp.registry.Node;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.event.NodeEvent;
import net.datattp.registry.event.NodeWatcher;
import net.datattp.registry.event.RegistryListener;
import net.datattp.registry.zk.RegistryImpl;

public class RegistryListenerUnitTest {
  static {
    System.setProperty("log4j.configuration", "file:src/test/resources/test-log4j.properties") ;
  }
  
  static public String TEST_PATH = "/node/event" ;
  static private EmbededZKServer zkServerLauncher ;
  
  @BeforeClass
  static public void startServer() throws Exception {
    FileUtil.removeIfExist("./build/data", false);
    zkServerLauncher = new EmbededZKServer("./build/data/zookeeper") ;
    zkServerLauncher.start();
  }
  
  static public void stopServer() throws Exception {
    zkServerLauncher.shutdown();
  }
  
  private Registry registry ;
  
  @Before
  public void setup() throws Exception {
    registry = newRegistry().connect();
  }
  
  @After
  public void teardown() throws Exception {
    registry.rdelete(TEST_PATH);
    registry.shutdown();
  }

  @Test
  public void testOneTimeCreateWatch() throws Exception {
    RegistryListener listener = new RegistryListener(registry);
    NodeEventCatcher nodeEventCatcher = new NodeEventCatcher();
    listener.watch(TEST_PATH, nodeEventCatcher, false);
    Node node = registry.createIfNotExist(TEST_PATH) ;
    Thread.sleep(100);
    Assert.assertEquals(0, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.CREATE, nodeEventCatcher.getNodeEvent().getType());
    Assert.assertEquals(TEST_PATH, nodeEventCatcher.getNodeEvent().getPath());
  }
  
  @Test
  public void testOneTimeModifyWatch() throws Exception {
    RegistryListener listener = new RegistryListener(registry);
    Node node = registry.createIfNotExist(TEST_PATH) ;
    NodeEventCatcher nodeEventCatcher = new NodeEventCatcher();
    listener.watch(TEST_PATH, nodeEventCatcher, false);
    node.setData(new byte[10]);
    Thread.sleep(100);
    Assert.assertEquals(0, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.MODIFY, nodeEventCatcher.getNodeEvent().getType());
    Assert.assertEquals(TEST_PATH, nodeEventCatcher.getNodeEvent().getPath());
  }
  
  @Test
  public void testPersistentWatch() throws Exception {
    RegistryListener listener = new RegistryListener(registry);
    NodeEventCatcher nodeEventCatcher = new NodeEventCatcher();
    listener.watch(TEST_PATH, nodeEventCatcher, true);
    Node node = registry.createIfNotExist(TEST_PATH) ;
    Thread.sleep(100);
    Assert.assertEquals(1, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.CREATE, nodeEventCatcher.getNodeEvent().getType());
    Assert.assertEquals(TEST_PATH, nodeEventCatcher.getNodeEvent().getPath());
    
    node.setData(new byte[10]);
    Thread.sleep(100);
    Assert.assertEquals(1, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.MODIFY, nodeEventCatcher.getNodeEvent().getType());
    Assert.assertEquals(TEST_PATH, nodeEventCatcher.getNodeEvent().getPath());
    
    listener.dump(System.out);
    listener.close();
  }
  
  @Test
  public void testChildrenWatch() throws Exception {
    RegistryListener listener = new RegistryListener(registry);
    ChildrenChangeEventCatcher childrenChangeEventCatcher = new ChildrenChangeEventCatcher();
    listener.watchChildren(TEST_PATH, childrenChangeEventCatcher, true, true);
    Node testNode = registry.createIfNotExist(TEST_PATH) ;
    listener.dump(System.out);
    Assert.assertEquals(1, listener.getWatchers().size());
    
    Node child1Node = registry.createIfNotExist(TEST_PATH + "/child-1") ;
    Thread.sleep(100);
    Assert.assertEquals(1, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.CHILDREN_CHANGED, childrenChangeEventCatcher.getNodeEvent().getType());
    
    Node child2Node = registry.createIfNotExist(TEST_PATH + "/child-2") ;
    listener.dump(System.out);
    listener.close();
  }
  
  @Test
  public void testChildWatch() throws Exception {
    RegistryListener listener = new RegistryListener(registry);
    ChildEventCatcher childEventCatcher = new ChildEventCatcher();
    listener.watchChild(TEST_PATH, "hello-.*", childEventCatcher);
    Node testNode = registry.createIfNotExist(TEST_PATH) ;
    System.out.println("\ncreate " + TEST_PATH);
    listener.dump(System.out);
    Assert.assertEquals(1, listener.getWatchers().size());
    
    System.out.println("\ncreate " + (TEST_PATH + "/child-1"));
    Node child1Node = registry.createIfNotExist(TEST_PATH + "/child-1") ;
    Thread.sleep(100);
    Assert.assertEquals(1, listener.getWatchers().size());
    
    System.out.println("\ncreate " + (TEST_PATH + "/hello-1"));
    Node child2Node = registry.createIfNotExist(TEST_PATH + "/hello-1") ;
    Thread.sleep(100);
    Assert.assertEquals(2, listener.getWatchers().size());
    Assert.assertEquals(NodeEvent.Type.CREATE, childEventCatcher.getNodeEvent().getType());
    Assert.assertEquals(TEST_PATH + "/hello-1", childEventCatcher.getNodeEvent().getPath());
    Thread.sleep(100);
    listener.dump(System.out);
    listener.close();
  }
  
  public class NodeEventCatcher extends NodeWatcher {
    private NodeEvent nodeEvent ;
    
    public void onEvent(NodeEvent event) {
      this.nodeEvent = event ;
    }
    
    public NodeEvent getNodeEvent() { return this.nodeEvent ; }
  }
  
  public class ChildrenChangeEventCatcher extends NodeWatcher {
    private NodeEvent nodeEvent ;
    
    public void onEvent(NodeEvent event) {
      if(event.getType() == NodeEvent.Type.CHILDREN_CHANGED) {
        this.nodeEvent = event ;
      }
    }
    
    public NodeEvent getNodeEvent() { return this.nodeEvent ; }
  }
  
  public class ChildEventCatcher extends NodeWatcher {
    private NodeEvent nodeEvent ;
    
    public void onEvent(NodeEvent event) {
      this.nodeEvent = event ;
    }
    
    public NodeEvent getNodeEvent() { return this.nodeEvent ; }
  }
  
  private Registry newRegistry() {
    return new RegistryImpl(RegistryConfig.getDefault()) ;
  }
}
