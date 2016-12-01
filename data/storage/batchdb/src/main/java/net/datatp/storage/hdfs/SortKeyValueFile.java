package net.datatp.storage.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.DefaultCodec;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class SortKeyValueFile<K extends WritableComparable, V extends Writable> {
  static DefaultCodec CODEC = new DefaultCodec() ;
  
  private FileSystem fs ;
  private String path ;
  private Class<K> keyType ;
  private Class<V> valueType ;
  private static DefaultCodec codec;
  
  public SortKeyValueFile(FileSystem fs, String path, Class<K> keyType, Class<V> valueType) {
    this(fs, path, keyType, valueType, CODEC);
  }
  
  public SortKeyValueFile(FileSystem fs, String path, Class<K> keyType, Class<V> valueType, DefaultCodec codec) {
    this.fs = fs ;
    this.path = path ;
    this.keyType = keyType; 
    this.valueType = valueType ;
    this.codec = codec ;
  }
  
  public SortKeyValueFile(Configuration conf, String path, Class<K> keyType, Class<V> valueType) throws IOException {
    this(FileSystem.get(conf), path, keyType, valueType) ;
  }

  public SortKeyValueFile(FileSystem fs, String dir, String fname, Class<K> keyType, Class<V> valueType) throws IOException {
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
    SequenceFile.Reader.Option[] opts = {
      SequenceFile.Reader.file(path)
    };
    SequenceFile.Reader reader = new SequenceFile.Reader(fs.getConf(), opts) ;
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
      Path writePath = new Path(path + ".writer");
      Path parentPath = writePath.getParent();
      
      SequenceFile.Writer.Option[] opts = {
        SequenceFile.Writer.file(writePath),
        SequenceFile.Writer.keyClass(keyType),
        SequenceFile.Writer.valueClass(valueType),
        SequenceFile.Writer.compression(CompressionType.RECORD,  codec),
        SequenceFile.Writer.replication(fs.getDefaultReplication(parentPath)),
        SequenceFile.Writer.blockSize(fs.getFileStatus(parentPath).getBlockSize()),
        SequenceFile.Writer.metadata(meta)
      };
      this.writer = SequenceFile.createWriter(fs.getConf(), opts);
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