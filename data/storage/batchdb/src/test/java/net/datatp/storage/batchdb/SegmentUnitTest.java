package net.datatp.storage.batchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.MultiSegmentIterator;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.Segment;
import net.datatp.storage.batchdb.TextUtil;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.io.FileUtil;

public class SegmentUnitTest {
  private String dblocation = "target/db" ;

  @Test
  public void test() throws Exception {
    FileUtil.removeIfExist(dblocation, false) ;

    assertString(new String[] { "0", "9", "1", "3" }, "first") ;
    assertText(new Text[] { new Text("0"), new Text("9"), new Text("1"), new Text("3") }, "second") ;
    assertInt(new int[] { 0, 9, 1, 3 }, "third") ;
    assertLong(new long[] { 0L, 9L, 1L, 3L }, "fourth") ;
  }
  
  private void assertInt(int[] keys, String prefix) throws IOException {
    String segname = Segment.createSegmentName(0) ;
    Segment segment =  new Segment(HDFSUtil.getDaultConfiguration(), dblocation, segname) ;
    
    List<byte[]> holder = new ArrayList<byte[]>() ;
    for(int i = 0; i < keys.length; i++) {
      holder.add(Bytes.toBytes(keys[i])) ;
    }
    assertBytes(holder.toArray(new byte[holder.size()][]), prefix, segment, keys.length) ;
  }
  
  private void assertLong(long[] keys, String prefix) throws IOException {
    String segname = Segment.createSegmentName(1) ;
    Segment segment =  new Segment(HDFSUtil.getDaultConfiguration(), dblocation, segname) ;
    
    List<byte[]> holder = new ArrayList<byte[]>() ;
    for(int i = 0; i < keys.length; i++) {
      holder.add(Bytes.toBytes(keys[i])) ;
    }
    assertBytes(holder.toArray(new byte[holder.size()][]), prefix, segment, keys.length) ;
  }
  
  private void assertText(Text[] keys, String prefix) throws IOException {
    String segname = Segment.createSegmentName(2) ;
    Segment segment =  new Segment(HDFSUtil.getDaultConfiguration(), dblocation, segname) ;
    
    List<byte[]> holder = new ArrayList<byte[]>() ;
    for(int i = 0; i < keys.length; i++) {
      holder.add(TextUtil.getBytes(keys[i])) ;
    }
    assertBytes(holder.toArray(new byte[holder.size()][]), prefix, segment, keys.length) ;
  }
  
  private void assertString(String[] keys, String prefix) throws IOException {
    String segname = Segment.createSegmentName(3) ;
    Segment segment =  new Segment(HDFSUtil.getDaultConfiguration(), dblocation, segname) ;
    
    List<byte[]> holder = new ArrayList<byte[]>() ;
    for(int i = 0; i < keys.length; i++) {
      holder.add(Bytes.toBytes(keys[i])) ;
    }
    assertBytes(holder.toArray(new byte[holder.size()][]), prefix, segment, keys.length) ;
  }
  
  
  private void assertBytes(byte[][] keys, String prefix, Segment segment, int expected) throws IOException {
    Segment.Writer writer = segment.getWriter() ;
    List<String> holder = new ArrayList<String>() ;
    for(int i = 0; i < keys.length; i++) {
      holder.add(Bytes.toString(keys[i])) ;
      RowId id = new RowId(keys[i], System.currentTimeMillis(), System.currentTimeMillis(), RowId.STORE_STATE) ;
      Cell value = new Cell() ;
      value.setName(holder.get(i)) ;
      if(prefix != null) value.addField("value", prefix + " " + holder.get(i)) ;
      else value.addField("value", holder.get(i)) ;
      value.setKey(id) ;
      writer.append(id, value) ;
    }
    writer.close() ;
    assertData(holder, prefix, segment, expected) ;
  }
  
  private void assertData(List<String> holder, String prefix, Segment segment, int expected) throws IOException {
    MultiSegmentIterator iterator = new MultiSegmentIterator(new Segment[] { segment }) ;
    Collections.sort(holder) ;
    int count = 0 ;
    while(iterator.next()) {
      RowId id = iterator.currentKey() ;
      Cell cell = iterator.currentValue() ;
      Assert.assertEquals(holder.get(count), Bytes.toString(id.getKey())) ;
      if(prefix != null) Assert.assertEquals(prefix + " " + holder.get(count), cell.getFieldAsString("value")) ;
      else Assert.assertEquals(holder.get(count), cell.getFieldAsString("value")) ;
      count++ ;
    };
    Assert.assertEquals(expected, count) ;
  }
}
