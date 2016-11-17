package net.datatp.es.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.util.dataformat.DataSerializer;

/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
//@Singleton
public class ElasticSearchService {
  private Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
  
  private Map<String, String> esProperties ;
  
  private Node server ;
  
  public Logger getLogger() { return this.logger; }
  
  public void start() throws Exception {
    Map<String, String> properties = new HashMap<String, String>() ;
    properties.put("cluster.name", "elasticsearch");
    properties.put("path.data",    "./build/elasticsearch");
    logger.info(
      "ElasticSearch default properties:\n" + 
      DataSerializer.JSON.toString(properties)
    );
    if(esProperties != null) {
      properties.putAll(esProperties);
      logger.info(
          "ElasticSearch overrided properties:\n" + 
          DataSerializer.JSON.toString(properties)
      );
    }
    
    Settings.Builder settingBuilder = Settings.builder();
    server = new Node(settingBuilder.build());
  }

  public void stop() throws IOException {
    server.close();
  }
}