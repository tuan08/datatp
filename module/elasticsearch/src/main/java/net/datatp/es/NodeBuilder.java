package net.datatp.es;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.MockNode;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.transport.MockTcpTransportPlugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.util.io.FileUtil;

public class NodeBuilder {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  private String clusterName = "elasticsearch";
  private String baseDir     = "build/elasticsearch";
  
  public Path createTempDir(String nodeName) { return new File( baseDir + "/" + nodeName).toPath(); }
  
  protected Collection<Class<? extends Plugin>> getMockPlugins() {
    ArrayList<Class<? extends Plugin>> mocks = new ArrayList<>();
//    mocks.add(MockTransportService.TestPlugin.class);
//    mocks.add(MockFSIndexStore.TestPlugin.class);
    
//    mocks.add(NodeMocksPlugin.class);
//    mocks.add(MockEngineFactoryPlugin.class);
//    mocks.add(MockSearchService.TestPlugin.class);
//
//    mocks.add(AssertingLocalTransport.TestPlugin.class);
    mocks.add(MockTcpTransportPlugin.class);
    mocks.add(Netty4Plugin.class);
//    mocks.add(TestSeedPlugin.class);
    return Collections.unmodifiableList(mocks);
  }
  
  public Node newNode() throws Exception {
    return newNode("es_node_0", 0);
  }
  
  public Node newNode(String nodeName, long seedId) throws Exception {
    logger.info("Create a new elasticsearch node: nodeNmae = " + nodeName + ", seedId = " + seedId);
    
    Path nodeHomeDir = createTempDir(nodeName);
    FileUtil.emptyFolder(nodeHomeDir.toFile(), false);
    
    Settings settings = Settings.builder().
        put(ClusterName.CLUSTER_NAME_SETTING.getKey(), clusterName).
        put(Environment.PATH_HOME_SETTING.getKey(), nodeHomeDir).
        put(Environment.PATH_REPO_SETTING.getKey(), nodeHomeDir.resolve("repo")).
        put("node.id.seed",Long.toString(seedId)).
        put(Environment.PATH_SHARED_DATA_SETTING.getKey(), nodeHomeDir.getParent()).
        put("node.name", nodeName).
        put("transport.tcp.port", "9300").
        put(NetworkModule.HTTP_TYPE_KEY, "netty4").
        put(NetworkModule.HTTP_ENABLED.getKey(), true).
        put("http.cors.enabled",  true).
        put("http.cors.allow-origin", "*").
        put("network.bind_host",  "0.0.0.0").
//        put("script.inline", true).
//        put("script.stored", true).
        put(ScriptService.SCRIPT_MAX_COMPILATIONS_PER_MINUTE.getKey(), 1000).
        put(EsExecutors.PROCESSORS_SETTING.getKey(), 1). // limit the number of threads created
        put("discovery.type", "local").
        //put("transport.type", "netty4").
        put(NetworkModule.TRANSPORT_TYPE_KEY, "netty4").
        put(Node.NODE_DATA_SETTING.getKey(), true).
        build();
    Node node = new MockNode(settings, getMockPlugins());
    node.start();
    
    ClusterHealthResponse clusterHealthResponse = node.client().admin().cluster().prepareHealth().setWaitForGreenStatus().get();
    Assert.assertFalse(clusterHealthResponse.isTimedOut());
    logger.info("Pass clusterHealthResponse.isTimedOut() 1");
    node.client().admin().indices().
      preparePutTemplate("random_index_template").
      setTemplate("*").
      setOrder(0).
      setSettings(Settings.builder().put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 1).put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 0)).get();
    
    return node;
  }
}
