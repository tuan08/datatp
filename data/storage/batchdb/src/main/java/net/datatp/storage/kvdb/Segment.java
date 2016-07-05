package net.datatp.storage.kvdb;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec;

import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.storage.hdfs.SortKeyValueFile;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 */
public class Segment<K extends WritableComparable, V extends Record> implements Comparable<Segment<K,V>>{
  static CompressionCodec DEFAULT_CODEC = new DefaultCodec() ;
  static CompressionCodec GZIP_CODEC    = new GzipCodec() ;
  
  private String                 dblocation;
  private String                 name;
  private int                    index;
  private Configuration          configuration;
  private Class<K>               keyType;
  private Class<V>               valueType;
  private FileSystem             fs;
  private SortKeyValueFile<K, V> segmentFile;

  public Segment(Configuration configuration, String dblocation, String name, 
                 Class<K> keyType, Class<V> valueType) throws Exception {
    this.configuration = configuration ;
    this.dblocation = dblocation ;
    this.name = name ;
    this.index = Integer.parseInt(name.substring(name.indexOf('-') + 1)) ;
    this.keyType = keyType; 
    this.valueType = valueType ;
    fs = FileSystem.get(configuration) ;
    HDFSUtil.mkdirs(fs, dblocation + "/" + name) ;
    segmentFile = new SortKeyValueFile<K, V>(fs, getDatFilePath(), keyType, valueType);
  }
  
  public String getName() { return this.name ; }
  
  public int getIndex() { return index ; }
  
  public String getDBLocation() { return this.dblocation ; }
  
  public String getSegmentPath() {
    return dblocation + "/" + name ;
  }
  
  public Configuration getConfiguration() { return this.configuration ; }
  
  public long getDataSize() throws Exception {
    return segmentFile.getDataSize();
  }
  
  public SequenceFile.Reader getReader() throws Exception {
    return segmentFile.getReader();
  }
  
  public void delete() throws IOException {
    segmentFile.delete();
    HDFSUtil.removeIfExists(fs, dblocation + "/" + name) ;
  }
  
  public SortKeyValueFile<K, V>.Writer getWriter() throws Exception {
    return segmentFile.getWriter(true);
  }
  
  private String getDatFilePath() {
    return dblocation + "/" + name + "/segment.dat" ;
  }

  public int compareTo(Segment<K, V> other) {
    return index - other.index;
  }
  
  static public String createSegmentName(int index) { return "segment-" + index ; }
}