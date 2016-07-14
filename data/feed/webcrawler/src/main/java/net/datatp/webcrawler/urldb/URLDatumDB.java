package net.datatp.webcrawler.urldb;


import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.springframework.beans.factory.annotation.Value;

import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.storage.kvdb.RecordDB;
import net.datatp.storage.kvdb.RecordMerger;
/**
 * Author : Tuan Nguyeni  
 *          tuan08@gmail.com
 * Apr 21, 2010  
 */
public class URLDatumDB extends RecordDB<Text, URLDatum> {
  @Value("${crawler.master.urldb.cleandb}")
  private boolean cleandb ;
  
  @Value("${crawler.master.urldb.dir}")
  private String dbLocation;
  
  public URLDatumDB() {}

  public URLDatumDB(String dbLocation, boolean cleanDb) throws Exception {
    this.dbLocation = dbLocation;
    this.cleandb    = cleanDb;
    onInit();
  }
  
  @PostConstruct
  public void onInit() throws Exception {
    Configuration conf = HDFSUtil.getDaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    if(cleandb) HDFSUtil.removeIfExists(fs, dbLocation) ;
    onInit(conf, dbLocation, Text.class, URLDatum.class);
    reload() ;
  }

  public boolean getCleanDB() { return this.cleandb ; }
  public void setCleanDB(boolean b) { cleandb = b ; }

  public URLDatumDB(Configuration configuration, String dblocation) throws Exception {
    super(configuration, dblocation, Text.class, URLDatum.class);
  }

  public Text createKey() { return new Text(); }

  public URLDatum createValue() { return new URLDatum(); }

  protected RecordMerger<URLDatum> createRecordMerger() { return new SegmentRecordMerger() ; }

  static public class SegmentRecordMerger implements RecordMerger<URLDatum> {
    public URLDatum merge(URLDatum r1, URLDatum r2) {
      if(r2.getCreatedTime() <= r1.getCreatedTime()) {
        return r2 ;
      }
      return r1 ;
    }
  }
}
