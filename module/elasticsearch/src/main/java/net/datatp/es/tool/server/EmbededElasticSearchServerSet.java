package net.datatp.es.tool.server;

import java.util.Map;

import net.datatp.tool.server.ServerSet;

public class EmbededElasticSearchServerSet extends ServerSet<ElasticSearchServer> {
  public EmbededElasticSearchServerSet(String baseDir, int basePort, int numOfServers, Map<String, String> serverProps) {
    super("elasticsearch", baseDir, basePort, numOfServers, serverProps);
  }

  @Override
  protected ElasticSearchServer newServer(int id, String serverName, String serverDir, int serverPort, Map<String, String> props) {
    ElasticSearchServer server = new ElasticSearchServer(serverName, "localhost", serverPort, serverDir + "/data");
    return server;
  }

}
