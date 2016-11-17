package net.datatp.es;

import java.util.Arrays;
import java.util.Collection;

import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.MockTcpTransportPlugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class HttpSmokeTestCase extends ESIntegTestCase {
  private static String nodeTransportTypeKey;
  private static String nodeHttpTypeKey;
  private static String clientTypeKey;

  @SuppressWarnings("unchecked")
  @BeforeClass
  public static void setUpTransport() {
    nodeTransportTypeKey = getTypeKey(randomFrom(MockTcpTransportPlugin.class, Netty4Plugin.class));
    nodeHttpTypeKey = getTypeKey(randomFrom(Netty4Plugin.class));
    clientTypeKey = getTypeKey(randomFrom(MockTcpTransportPlugin.class, Netty4Plugin.class));
  }

  private static String getTypeKey(Class<? extends Plugin> clazz) {
    if (clazz.equals(MockTcpTransportPlugin.class)) {
      return MockTcpTransportPlugin.MOCK_TCP_TRANSPORT_NAME;
    //} else if (clazz.equals(Netty3Plugin.class)) {
    //  return Netty3Plugin.NETTY_TRANSPORT_NAME;
    } else {
      assert clazz.equals(Netty4Plugin.class);
      return Netty4Plugin.NETTY_TRANSPORT_NAME;
    }
  }

  @Override
  protected Settings nodeSettings(int nodeOrdinal) {
    logger.info("nodeHttpTypeKey = " + nodeHttpTypeKey);
    logger.info("nodeTransportTypeKey = " + nodeTransportTypeKey);
    return Settings.builder()
        .put(super.nodeSettings(nodeOrdinal))
        .put(NetworkModule.TRANSPORT_TYPE_KEY, nodeTransportTypeKey)
        .put(NetworkModule.HTTP_TYPE_KEY, nodeHttpTypeKey)
        .put(NetworkModule.HTTP_ENABLED.getKey(), true).build();
  }

  @Override
  protected Collection<Class<? extends Plugin>> nodePlugins() {
    return Arrays.asList(MockTcpTransportPlugin.class, Netty4Plugin.class);
  }

  @Override
  protected Collection<Class<? extends Plugin>> transportClientPlugins() {
    return Arrays.asList(MockTcpTransportPlugin.class, Netty4Plugin.class);
  }

  @Override
  protected Settings transportClientSettings() {
    logger.info("clientTypeKey = " + clientTypeKey);
    return Settings.builder()
        .put(super.transportClientSettings())
        .put(NetworkModule.TRANSPORT_TYPE_KEY, clientTypeKey)
        .build();
  }

  @Override
  protected boolean ignoreExternalCluster() { return true; }
  
  @Test
  public void test() throws Exception {
    Thread.sleep(3000);
  }

}