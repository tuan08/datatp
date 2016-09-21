package net.datatp.crawler.disributed.urldb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

import net.datatp.crawler.distributed.urldb.URLDatumRecord;
import net.datatp.crawler.distributed.urldb.URLDatumRecordDB;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.MergeMultiSegmentIterator;
import net.datatp.storage.kvdb.Segment;
import net.datatp.util.io.FileUtil;

public class URLDatumDBUnitTest {
  URLDatumRecordDB db ;
  
  @Before
  public void setup() throws Exception {
    FileUtil.mkdirs("build/urldb");
    db = new URLDatumRecordDB("build/urldb", true);
  }
  
  @Test
  public void testURLDatumSize() throws Exception {
    URLDatumRecord urldatum = new URLDatumRecord(System.currentTimeMillis());
    urldatum.setOrginalUrl("http://vnexpress.net/tin-tuc/giao-duc/de-an-ngoai-ngu-gan-9-400-ty-sau-8-nam-lam-duoc-nhung-gi-3470788.html");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    urldatum.write(new DataOutputStream(os));
    os.close();
    System.out.println("Data Size: " + os.toByteArray().length);
  }
  
  @Test
  public void test() throws Exception {
    Segment<Text,URLDatumRecord> segment0 = db.newSegment();
    writeSegment(segment0, "http://vnexpress.net", URLDatum.STATUS_NEW, 10);
    dumpDB(db);
    
    MergeMultiSegmentIterator<Text, URLDatumRecord> i = db.getMergeRecordIterator();
    Segment<Text, URLDatumRecord> segment2 = db.newSegment();
    SortKeyValueFile<Text, URLDatumRecord>.Writer writer = segment2.getWriter();
    while(i.next()) {
      URLDatumRecord urlDatum = i.currentValue();
      urlDatum.setStatus(URLDatum.STATUS_WAITING);
      writer.append(new Text(urlDatum.getOriginalUrl()), urlDatum);
    }
    writer.close();
    dumpDB(db);
    System.out.println("compact");
    db.autoCompact();
    dumpDB(db);
  }
  
  void writeSegment(Segment<Text, URLDatumRecord> segment, String baseUrl, byte status, int size) throws Exception {
    SortKeyValueFile<Text, URLDatumRecord>.Writer writer = segment.getWriter();
    for(int i = 0; i < 10; i++) {
      URLDatumRecord urldatum = new URLDatumRecord(System.currentTimeMillis());
      urldatum.setOrginalUrl(baseUrl + "/" + i);
      urldatum.setStatus(status);
      writer.append(new Text(urldatum.getOriginalUrl()), urldatum);
    }
    writer.close();
  }
  
  void dumpDB(URLDatumRecordDB db) throws Exception {
    MergeMultiSegmentIterator<Text, URLDatumRecord> i = db.getMergeRecordIterator();
    while(i.next()) {
      URLDatum urlDatum = i.currentValue();
      System.out.println(urlDatum.getOriginalUrl() + ", status = " + urlDatum.getStatus());
    }
  }
}
