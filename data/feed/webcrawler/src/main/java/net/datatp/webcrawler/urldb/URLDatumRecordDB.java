package net.datatp.webcrawler.urldb;


import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.springframework.beans.factory.annotation.Value;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;
import net.datatp.http.crawler.urldb.URLDatumDBIterator;
import net.datatp.http.crawler.urldb.URLDatumDBWriter;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.MergeMultiSegmentIterator;
import net.datatp.storage.kvdb.RecordDB;
import net.datatp.storage.kvdb.RecordMerger;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 21, 2010  
 */
public class URLDatumRecordDB extends RecordDB<Text, URLDatumRecord> implements URLDatumDB {
  @Value("${crawler.master.urldb.cleandb}")
  private boolean cleandb ;
  
  @Value("${crawler.master.urldb.dir}")
  private String dbLocation;
  
  public URLDatumRecordDB() {}

  public URLDatumRecordDB(String dbLocation, boolean cleanDb) throws Exception {
    this.dbLocation = dbLocation;
    this.cleandb    = cleanDb;
    onInit();
  }
  
  @PostConstruct
  public void onInit() throws Exception {
    Configuration conf = HDFSUtil.getDaultConfiguration() ;
    FileSystem fs = FileSystem.get(conf) ;
    if(cleandb) HDFSUtil.removeIfExists(fs, dbLocation) ;
    onInit(conf, dbLocation, Text.class, URLDatumRecord.class);
    reload() ;
  }

  public boolean getCleanDB() { return this.cleandb ; }
  public void setCleanDB(boolean b) { cleandb = b ; }

  public URLDatumRecordDB(Configuration configuration, String dblocation) throws Exception {
    super(configuration, dblocation, Text.class, URLDatumRecord.class);
  }

  public Text createKey() { return new Text(); }

  public URLDatumRecord createValue() { return new URLDatumRecord(); }

  protected RecordMerger<URLDatumRecord> createRecordMerger() { return new SegmentRecordMerger() ; }

  public URLDatumDBWriter createURLDatumDBWriter() throws Exception { 
    return new URLDatumRecordDBWriter();
  }
  
  public URLDatumDBIterator createURLDatumDBIterator() throws Exception { 
    return new URLDatumRecordDBIterator();
  }
  
  public class URLDatumRecordDBWriter  implements URLDatumDBWriter {
    private SortKeyValueFile<Text, URLDatumRecord>.Writer writer;
    
    public URLDatumRecordDBWriter() throws Exception {
      writer = newSegment().getWriter();
    }
    
    public URLDatum createURLDatumInstance(long timestamp) {
      return new URLDatumRecord(timestamp);
    }
    
    public void write(URLDatum urlDatum) throws IOException {
      URLDatumRecord record = (URLDatumRecord) urlDatum;
      writer.append(new Text(record.getId()), record) ;
    }
    
    public void optimize() throws Exception {
      autoCompact();
    }
    
    public void close() throws IOException {
      writer.close();
    }
  }

  
  public class URLDatumRecordDBIterator implements URLDatumDBIterator {
    private MergeMultiSegmentIterator<Text, URLDatumRecord> mitr;
    private URLDatum currentURLDatum;
    
    public URLDatumRecordDBIterator() throws Exception {
      mitr = getMergeRecordIterator() ;
    }
    
    public boolean hasNext() throws IOException {
      currentURLDatum = null;
      if(mitr.next()) {
        currentURLDatum = mitr.currentValue() ;
        return true;
      }
      return false;
    }
    
    public URLDatum next() {
      URLDatum ret = currentURLDatum;
      currentURLDatum = null;
      return ret;
    }
    
    public void close() throws IOException {
      mitr.close();
    }
  }
  
  static public class SegmentRecordMerger implements RecordMerger<URLDatumRecord> {
    public URLDatumRecord merge(URLDatumRecord r1, URLDatumRecord r2) {
      if(r2.getCreatedTime() <= r1.getCreatedTime()) {
        return r2 ;
      }
      return r1 ;
    }
  }
}
