package net.datatp.webcrawler.urldb;

import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.MergeMultiSegmentIterator;
import net.datatp.storage.kvdb.Segment;
import net.datatp.util.io.FileUtil;

public class URLDatumDBUnitTest {
  URLDatumDB db ;
  
  @Before
  public void setup() throws Exception {
    FileUtil.mkdirs("build/urldb");
    db = new URLDatumDB("build/urldb", true);
  }
  
  @Test
  public void test() throws Exception {
    Segment<Text,URLDatum> segment0 = db.newSegment();
    writeSegment(segment0, "http://vnexpress.net", URLDatum.STATUS_NEW, 10);
    dumpDB(db);
    
    MergeMultiSegmentIterator<Text, URLDatum> i = db.getMergeRecordIterator();
    Segment<Text,URLDatum> segment2 = db.newSegment();
    SortKeyValueFile<Text, URLDatum>.Writer writer = segment2.getWriter();
    while(i.next()) {
      URLDatum urlDatum = i.currentValue();
      urlDatum.setStatus(URLDatum.STATUS_WAITING);
      writer.append(new Text(urlDatum.getOriginalUrl()), urlDatum);
    }
    writer.close();
    dumpDB(db);
    System.out.println("compact");
    db.autoCompact();
    dumpDB(db);
  }
  
  void writeSegment(Segment<Text,URLDatum> segment, String baseUrl, byte status, int size) throws Exception {
    SortKeyValueFile<Text, URLDatum>.Writer writer = segment.getWriter();
    for(int i = 0; i < 10; i++) {
      URLDatum urldatum = new URLDatum(System.currentTimeMillis());
      urldatum.setOrginalUrl(baseUrl + "/" + i);
      urldatum.setStatus(status);
      writer.append(new Text(urldatum.getOriginalUrl()), urldatum);
    }
    writer.close();
  }
  
  void dumpDB(URLDatumDB db) throws Exception {
    MergeMultiSegmentIterator<Text, URLDatum> i = db.getMergeRecordIterator();
    while(i.next()) {
      URLDatum urlDatum = i.currentValue();
      System.out.println(urlDatum.getFetchUrl() + ", status = " + urlDatum.getStatus());
    }
  }
}
