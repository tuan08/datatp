package net.datatp.storage.batchdb;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec ;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec ;

import net.datatp.storage.hdfs.HDFSUtil;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class Segment implements Comparable<Segment>{
  static CompressionCodec DEFAULT_CODEC = new DefaultCodec() ;
  static CompressionCodec GZIP_CODEC    = new GzipCodec() ;

  private String dblocation ;
  private String name ;
  private int    index   ;
  private Configuration configuration ;

  public Segment(Configuration configuration, String dblocation, String name) throws IOException {
    this.configuration = configuration ;
    this.dblocation = dblocation ;
    this.name = name ;
    this.index = Integer.parseInt(name.substring(name.indexOf('-') + 1)) ;
    HDFSUtil.mkdirs(FileSystem.get(configuration), dblocation + "/" + name) ;
  }

  public String getName() { return this.name ; }

  public int getIndex() { return index ; }

  public String getLocation() { return this.dblocation ; }

  public String getSegmentPath() {
    return dblocation + "/" + name ;
  }

  public Configuration getConfiguration() { return this.configuration ; }

  public long getDataSize() throws IOException {
    FileSystem fs = FileSystem.get(this.configuration) ;
    Path path = new Path(getDatFilePath()) ;
    if(!fs.exists(path)) return 0 ;
    FileStatus status = fs.getFileStatus(path) ;
    return status.getLen() ;
  }

  public SequenceFile.Reader getReader() throws IOException {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    Path path = new Path(getDatFilePath()) ;
    if(!fs.exists(path)) return null ;
    SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf) ;
    return reader ;
  }

  public void delete() throws IOException {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    if(!fs.delete(new Path(getSegmentPath()), true)) {
      throw new IOException("Cannot delete path: " + getSegmentPath()) ;
    }
  }

  public Writer getWriter() throws IOException {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    return new Writer(fs, getDatFilePath()) ;
  }

  private String getDatFilePath() {
    return dblocation + "/" + name + "/segment.dat" ;
  }

  public int compareTo(Segment other) {
    return index - other.index;
  }

  static public String createSegmentName(int index) {
    return "segment-" + index ;
  }

  public class Writer {
    final public String path ;
    final SequenceFile.Writer writer ;
    final FileSystem fs ;
    private RowId pkey ;
    private boolean keyOutOfOrder ;

    public Writer(FileSystem fs, String path) throws IOException {
      Configuration conf = fs.getConf() ;
      String compressionCodec = conf.get("compression.default.codec") ;
      CompressionCodec codec  = DEFAULT_CODEC;
      if("gzip".equals(compressionCodec)) codec = GZIP_CODEC ;
      else if("lzo".equals(compressionCodec)) codec = GZIP_CODEC ; //TODO: fix lzo codec

      this.path = path ;
      this.fs = fs ;
      SequenceFile.Metadata meta = new SequenceFile.Metadata() ;
      Path writePath = new Path(path + ".writer");
      FileStatus dbPathStatus = fs.getFileStatus(new Path(dblocation));
      SequenceFile.Writer.Option[] opts = {
        SequenceFile.Writer.file(writePath),
        SequenceFile.Writer.keyClass(RowId.class),
        SequenceFile.Writer.valueClass(Cell.class),
        SequenceFile.Writer.compression(CompressionType.RECORD,  codec),
        SequenceFile.Writer.replication(dbPathStatus.getReplication()),
        SequenceFile.Writer.blockSize(dbPathStatus.getBlockSize()),
        SequenceFile.Writer.metadata(meta)
      };
      this.writer = SequenceFile.createWriter(fs.getConf(), opts);
//      this.writer = SequenceFile.createWriter(
//          fs,                   //FileSystem 
//          fs.getConf(),  // configuration 
//          new Path(path + ".writer"),          // the path
//          RowId.class,            // Key Class
//          Cell.class,      // Value Class
//          fs.getConf().getInt("io.file.buffer.size", 4096), //buffer size 
//          fs.getDefaultReplication(),  //frequency of replication 
//          fs.getDefaultBlockSize(), // default to 32MB: large enough to minimize the impact of seeks 
//          SequenceFile.CompressionType.BLOCK,   //Compress method
//          codec,      // compression codec
//          null,                  //progressor
//          meta            // File Metadata
//          ) ;
    }

    public void append(RowId key, Cell value) throws IOException {
      this.writer.append(key, value) ;
      if(!keyOutOfOrder && pkey != null) {
        if(pkey.compareTo(key) > 0) keyOutOfOrder = true ;
      }
      pkey = key ;
    }

    public void close() throws IOException {
      this.writer.close() ;
      SequenceFile.Sorter sorter = new SequenceFile.Sorter(fs, RowId.class, Cell.class, fs.getConf()) ;
      Path pwriter = new Path(path + ".writer") ;
      if(keyOutOfOrder) {
        Path psort   = new Path(path + ".sort") ;
        sorter.sort(new Path[]{pwriter}, psort, true) ;
        renameWithBak(fs, psort, new Path(path)) ;
      } else {
        renameWithBak(fs, pwriter, new Path(path)) ;
      }
    }

    private void renameWithBak(FileSystem fs , Path src, Path dest) throws IOException {
      Path bakPath = null ;
      if(fs.exists(dest)) {
        bakPath = new Path(dest.toString() + ".bak") ;
        HDFSUtil.mv(fs, dest, bakPath) ;
      }
      HDFSUtil.mv(fs, src, dest) ;
      if(bakPath != null) HDFSUtil.removeIfExists(fs, bakPath) ;
    }
  }
}