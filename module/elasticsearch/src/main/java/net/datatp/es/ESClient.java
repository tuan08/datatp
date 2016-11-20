package net.datatp.es;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * $Author: Tuan Nguyen$
 **/
public class ESClient {
  
  protected Client client;
  private String[] address;

  public ESClient(Client client) {
    this.client = client;
  }
  
  public ESClient(String[] address) throws UnknownHostException {
    this("elasticsearch", address);
  }
  
  public ESClient(String clusterName, String[] address) throws UnknownHostException {
    this.address = address;
    Settings settings = 
      Settings.builder().
      put("cluster.name", clusterName).
      put("transport.ping_schedule", "20s").
      put(NetworkModule.TRANSPORT_TYPE_KEY, "netty4").
      put(NetworkModule.HTTP_TYPE_KEY,      "netty4").
      build();
    
    ESTransportClient transportClient = new ESTransportClient(settings);
    
    for (String selAddr : address) {
      int port = 9300;
      if (selAddr.indexOf(":") > 0) {
        port = Integer.parseInt(selAddr.substring(selAddr.indexOf(":") + 1));
        selAddr = selAddr.substring(0, selAddr.indexOf(":"));
      }
      transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(selAddr), port));
    }
    this.client = transportClient;
  }
  
  public String[] getAddress() { return this.address; }

  public boolean waitForConnected(long timeout) throws InterruptedException {
    if(client instanceof TransportClient) {
      TransportClient transportClient = (TransportClient) client;
      long stopTime = System.currentTimeMillis() + timeout ;
      while(System.currentTimeMillis() < stopTime) {
        List<DiscoveryNode> nodes  = transportClient.connectedNodes() ;
        if(!nodes.isEmpty()) return true ;
        Thread.sleep(1000);
      }
      return false ;
    }
    return true;
  }
  
  public void createIndex(String index, String settings) throws Exception {
    CreateIndexRequestBuilder builder = client.admin().indices().prepareCreate(index);
    if (settings != null) {
      builder.setSettings(settings);
    }
    CreateIndexResponse response = builder.execute().actionGet();
  }

  public void optimizeIndex(String index) throws Exception {
    ActionFuture<ForceMergeResponse> action = client.admin().indices().forceMerge(new ForceMergeRequest(index));
    ForceMergeResponse response = action.actionGet();
    System.out.println("Optimize Failed Shard: " + response.getFailedShards());
  }

  public void removeIndex(String index) throws Exception {
    DeleteIndexRequestBuilder builder = client.admin().indices().prepareDelete(index);
    DeleteIndexResponse response = builder.execute().actionGet();
  }

  public void updateMapping(String index, String type, String mapping) throws Exception {
    PutMappingRequestBuilder builder = client.admin().indices().preparePutMapping(index);
    builder.setType(type);
    builder.setSource(mapping);
    PutMappingResponse response = builder.execute().actionGet();
  }

  public void updateSettings(String index, String settings) throws Exception {
    UpdateSettingsRequestBuilder builder = client.admin().indices().prepareUpdateSettings(index);
    builder.setSettings(settings);
    UpdateSettingsResponse response = builder.execute().actionGet();
  }

  public ClusterState getClusterState() {
    ClusterStateRequestBuilder stateBuilder = client.admin().cluster().prepareState();
    ClusterStateResponse response = stateBuilder.execute().actionGet();
    ClusterState state = response.getState();
    return state;
  }

  public NodeInfo getNodeInfo(String nodeId) {
    NodesInfoRequestBuilder builder = client.admin().cluster().prepareNodesInfo(nodeId);
    NodesInfoResponse response = builder.execute().actionGet();
    return response.getNodes().get(0);
  }

  public NodeInfo[] getNodeInfo(String... nodeId) {
    NodesInfoRequestBuilder builder = client.admin().cluster().prepareNodesInfo(nodeId);
    NodesInfoResponse response = builder.execute().actionGet();
    List<NodeInfo> holder = response.getNodes();
    return holder.toArray(new NodeInfo[holder.size()]);
  }

  public boolean hasIndex(String name) {
    String[] indices = this.getClusterState().metaData().getConcreteAllIndices();
    for (String sel : indices) {
      if (sel.equals(name))
        return true;
    }
    return false;
  }

  public Map<String, IndexStats> getIndexStats() {
    String[] indices = getClusterState().metaData().getConcreteAllIndices();
    IndicesStatsRequestBuilder builder = client.admin().indices().prepareStats(indices);
    IndicesStatsResponse response = builder.execute().actionGet(); 
    Map<String, IndexStats> stats = response.getIndices() ;
    return stats;
  }
  
  public void close() { client.close(); }
  
  
  static public class ESTransportClient extends TransportClient {
    private static final Collection<Class<? extends Plugin>> PRE_INSTALLED_PLUGINS =
      Arrays.asList(Netty4Plugin.class, ReindexPlugin.class, PercolatorPlugin.class, MustachePlugin.class);

    @SafeVarargs
    public ESTransportClient(Settings settings, Class<? extends Plugin>... plugins) {
      this(settings, Arrays.asList(plugins));
    }

    public ESTransportClient(Settings settings, Collection<Class<? extends Plugin>> plugins) {
      super(settings, Settings.EMPTY, addPlugins(plugins, PRE_INSTALLED_PLUGINS));
    }

    @Override
    public void close() {
      super.close();
      if (NetworkModule.TRANSPORT_TYPE_SETTING.exists(settings) == false
          || NetworkModule.TRANSPORT_TYPE_SETTING.get(settings).equals(Netty4Plugin.NETTY_TRANSPORT_NAME)) {
        try {
          GlobalEventExecutor.INSTANCE.awaitInactivity(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        try {
          ThreadDeathWatcher.awaitInactivity(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}