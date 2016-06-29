package net.datatp.storage.batchdb.mapred;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jun 7, 2010  
 */
public class MapInputFormat extends InputFormat<Text, MapInputSplit> {

  public RecordReader<Text, MapInputSplit> createRecordReader(InputSplit isplit, TaskAttemptContext taContext) throws IOException, InterruptedException {
    return new RecordReaderImpl() ;
  }

  public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
    List<InputSplit> holder = new ArrayList<InputSplit>() ;
    Configuration conf = context.getConfiguration() ;
    String maps = conf.get("maps") ;
    String[] map = maps.split(",") ;
    for(String selMap : map) {
      Iterator<Map.Entry<String, String>> i = conf.iterator() ; 
      Map<String, String> props = new HashMap<String, String>() ;
      while(i.hasNext()) {
        Map.Entry<String, String> entry = i.next() ;
        String key = entry.getKey() ;
        if(key.startsWith(selMap)) {
          props.put(key.substring(selMap.length() + 1), entry.getValue()) ;
        }
      }
      MapInputSplit split = new MapInputSplit(selMap, props) ;
      holder.add(split) ;
    }
    return holder;
  }
  
  static public void addMap(Configuration conf, String id, Map<String, String> properties) {
    String maps = conf.get("maps") ;
    if(maps == null) maps = id ;
    else maps += "," + id ;
    conf.set("maps", maps) ;
    
    Iterator<Map.Entry<String, String>> i = properties.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, String> entry = i.next() ;
      conf.set(id + ":" + entry.getKey(), entry.getValue()) ;
    }
  }
  
  static public class RecordReaderImpl extends RecordReader<Text, MapInputSplit> {
    private MapInputSplit split ;
    private boolean read = false ;
    
    public void initialize(InputSplit split, TaskAttemptContext taContext) throws IOException, InterruptedException {
      this.split = (MapInputSplit) split ;
    }
    
    public Text getCurrentKey() throws IOException, InterruptedException {
      return new Text(split.getId()) ;
    }

    public MapInputSplit getCurrentValue() throws IOException, InterruptedException {
      read = true ;
      return split ;
    }

    public float getProgress() throws IOException, InterruptedException {
      return 0;
    }

    public boolean nextKeyValue() throws IOException, InterruptedException {
      return !read ;
    }
    
    public void close() throws IOException {
    }
  }
}