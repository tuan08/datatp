package net.datatp.storage.batchdb.mapred;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jun 7, 2010  
 */
public class MapInputSplit extends InputSplit implements Writable {
  private String id ;
  private Map<String, String> properties ;
  
  public MapInputSplit() {} 
  
  public MapInputSplit(String id, Map<String, String> properties) {
    this.id = id ;
    this.properties = properties ;
  }
  
  public String getId() { return this.id ; }
  
  public Map<String, String> getProperties() { return this.properties ; }
  
  public long getLength() throws IOException, InterruptedException { return 0; }

  public String[] getLocations() throws IOException, InterruptedException { return new String[0] ; }

  public void readFields(DataInput in) throws IOException {
    this.id = Text.readString(in) ;
    int size = in.readInt() ;
    this.properties = new HashMap<String, String>() ;
    for(int i = 0; i < size; i++) {
      String key = Text.readString(in) ;
      String value = Text.readString(in) ;
      this.properties.put(key, value) ;
    }
  }

  public void write(DataOutput out) throws IOException {
    Text.writeString(out, id); 
    out.writeInt(properties.size()) ;
    Iterator<Map.Entry<String, String>> i = properties.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, String> entry = i.next() ;
      Text.writeString(out, entry.getKey()) ;
      Text.writeString(out, entry.getValue()) ;
    }
  }
}