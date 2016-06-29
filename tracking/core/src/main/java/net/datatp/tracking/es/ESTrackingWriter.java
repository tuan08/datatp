package net.datatp.tracking.es;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.tracking.TrackingMessage;
import net.datatp.tracking.TrackingRegistry;
import net.datatp.tracking.TrackingWriter;
import net.datatp.util.text.StringUtil;

public class ESTrackingWriter extends TrackingWriter {
  @Parameter(names = "--es-connect", description = "Elasticsearch connect address")
  private String esConnects = "127.0.0.1:9300";
  
  @Parameter(names = "--es-index", description = "The index")
  private String indexName  = "tracking-message";
  
  private ESObjectClient<TrackingMessage> esObjecClient;
  private Map<String, TrackingMessage> buffer = new HashMap<String, TrackingMessage>(); 
  
  public ESTrackingWriter(String[] args) throws Exception {
    new JCommander(this, args);
  }
  
  @Override
  synchronized public void onInit(TrackingRegistry registry) throws Exception {
    System.out.println("[ESTrackingWriter]: esConnects = " + esConnects);
    String[] esConnect = StringUtil.toStringArray(esConnects);
    esObjecClient = new ESObjectClient<TrackingMessage>(new ESClient(esConnect), indexName, TrackingMessage.class) ;
    if(!esObjecClient.getESClient().hasIndex(indexName)) {
      esObjecClient.createIndex();
    }
    System.out.println("[ ESTrackingWriter] onInit()");
  }
 
  @Override
  synchronized public void onDestroy(TrackingRegistry registry) throws Exception {
    flush();
    esObjecClient.close();
    System.out.println("[ ESTrackingWriter] onDestroy()");
  }
  
  @Override
  synchronized public void write(TrackingMessage message) throws Exception {
    buffer.put(message.uniqueId(), message);
    if(buffer.size() > 100) flush();
  }
  
  void flush() {
    if(buffer.size() == 0) return;
    Map<String, TrackingMessage> bufferToFlush = null;
    synchronized(ESTrackingWriter.this) {
      bufferToFlush = buffer;
      buffer = new HashMap<String, TrackingMessage>();
    }
    esObjecClient.put(bufferToFlush);
  }
}
