package net.datatp.es;

import org.apache.lucene.util.IOUtils;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;

@RunWith(RandomizedRunner.class)
@ThreadLeakScope(Scope.SUITE)
@ThreadLeakLingering(linger = 3000)
public class NodeBuilderUnitTest {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  Node node;
  
  @Before
  public void setup() throws Exception {
    NodeBuilder nBuilder = new NodeBuilder();
    node = nBuilder.newNode();
    logger.info("Node Name: " + node.settings().get("node.name"));
    logger.info("Port     : " + node.settings().get("transport.tcp.port"));
  }

  @After
  public void teardown() throws Exception {
    IOUtils.close(node);
  }

  @Test
  public void test() throws Exception {
    ESClient esclient = new ESClient("elasticsearch", new String[] { "127.0.0.1:9300"});
    logger.info("ClusterState: " + esclient.getClusterState().prettyPrint());
    esclient.close();
  }
}
