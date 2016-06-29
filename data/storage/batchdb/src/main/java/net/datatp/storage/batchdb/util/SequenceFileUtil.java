package net.datatp.storage.batchdb.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.DefaultCodec;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class SequenceFileUtil<K extends WritableComparable, V extends Writable> {
  static DefaultCodec CODEC = new DefaultCodec() ;
  
  private FileSystem fs ;
  private String path ;
  private Class<K> keyType ;
  private Class<V> valueType ;
  
  public SequenceFileUtil(FileSystem fs, String path, Class<K> keyType, Class<V> valueType) {
    this.fs = fs ;
    this.path = path ;
    this.keyType = keyType; 
    this.valueType = valueType ;
  }
  
  public SequenceFileUtil(Configuration conf, String path, Class<K> keyType, Class<V> valueType) throws IOException {
    this(FileSystem.get(conf), path, keyType, valueType) ;
  }

  public SequenceFileUtil(FileSystem fs, String dir, String fname, Class<K> keyType, Class<V> valueType) throws IOException {
    this(fs, dir + "/" + fname, keyType, valueType) ;
    HDFSUtil.mkdirs(fs, dir) ;
  }
  
  public String getPath() { return path; }
  
  public long getDataSize() throws Exception {
    Path path = new Path(getPath()) ;
    if(!fs.exists(path)) return 0 ;
    FileStatus status = fs.getFileStatus(path) ;
    return status.getLen() ;
  }
  
  public SequenceFile.Reader getReader() throws IOException {
    Path path = new Path(getPath()) ;
    if(!fs.exists(path)) return null ;
    SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, fs.getConf()) ;
    return reader ;
  }
  
  public void delete() throws IOException {
    if(!fs.delete(new Path(getPath()), true)) {
      throw new IOException("Cannot delete path: " + getPath()) ;
    }
  }
  
  public Writer getWriter(boolean sortAtClose) throws IOException { 
    return new Writer(fs, getPath(), sortAtClose) ; 
  }
  
  public class Writer {
    final public String path ;
    final SequenceFile.Writer writer ;
    final FileSystem fs ;
    private boolean sortAtClose = true ;
    
    public Writer(FileSystem fs, String path, boolean sortAtClose) throws IOException {
      this.fs = fs ;
      this.path = path ;
      this.sortAtClose = sortAtClose ;
      SequenceFile.Metadata meta = new SequenceFile.Metadata() ;
      this.writer = SequenceFile.createWriter(
          fs,                   //FileSystem 
          fs.getConf(),  // configuration 
          new Path(path + ".writer"),          // the path
          keyType,            // Key Class
          valueType,      // Value Class
          fs.getConf().getInt("io.file.buffer.size", 4096), //buffer size 
          fs.getDefaultReplication(),  //frequency of replication 
          fs.getDefaultBlockSize(), // default to 32MB: large enough to minimize the impact of seeks 
          SequenceFile.CompressionType.BLOCK,   //Compress method
          CODEC,      // compression codec
          null,       //progressor
          meta        // File Metadata
      ) ;
    }

    public void append(K key, V value) throws IOException {
      this.writer.append(key, value) ;
    }
    
    public void close() throws IOException {
      this.writer.close() ;
      Path pwriter = new Path(path + ".writer") ;
      if(sortAtClose) {
        SequenceFile.Sorter sorter = new SequenceFile.Sorter(fs, keyType, valueType, fs.getConf()) ;
        Path psort   = new Path(path + ".sort") ;
        sorter.sort(new Path[]{pwriter}, psort, true) ;
        HDFSUtil.removeIfExists(fs, path) ;
        HDFSUtil.mv(fs, psort, new Path(path)) ;
      } else {
        HDFSUtil.mv(fs, pwriter, new Path(path)) ;
      }
    }
  }
}