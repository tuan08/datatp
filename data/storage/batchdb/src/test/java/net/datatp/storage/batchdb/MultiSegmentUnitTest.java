package net.datatp.storage.batchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.CellMerger;
import net.datatp.storage.batchdb.MultiSegmentIterator;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.Segment;
import net.datatp.storage.batchdb.util.HDFSUtil;
import net.datatp.util.io.FileUtil;

public class MultiSegmentUnitTest {

  @Test
  public void test() throws Exception {
    String dblocation = "target/db" ;
    FileUtil.removeIfExist(dblocation, false) ;
    Segment segment0 = 
      new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(0)) ;
    Segment segment1 = 
      new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(1)) ;
    
    assertData(new String[] { "0", "9" }, "first",  segment0, 2) ;
    assertData(new String[] { "0", "1" }, "second", segment1, 2) ;
    
    assertMultiSegmentIterator(
      new Segment[] { segment0, segment1 },
      new Result[] {
          new Result("0", "first 0",  getCreatedTime(-1)),
          new Result("0", "second 0", getCreatedTime(-2)),
          new Result("1", "second 1", getCreatedTime(-2)),
          new Result("9", "first 9",  getCreatedTime(-1)) },
          false) ;
    
    assertMultiSegmentIterator(
      new Segment[] { segment0, segment1 },
      new Result[] { 
          new Result("0", "second 0", getCreatedTime(-1)),
          new Result("1", "second 1", getCreatedTime(-2)),
          new Result("9", "first 9",  getCreatedTime(-1)) },
      true) ;
  }
  
  private long getCreatedTime(int increment) {
    Calendar cal = Calendar.getInstance() ;
    cal.add(Calendar.DAY_OF_MONTH, increment) ;
    return cal.getTimeInMillis() ;
  }
  
  class Result {
    private String key ;
    private String value ;
    private long createdTime ;
    
    Result(String key, String value, long createdTime) {
      this.key = key ;
      this.value = value ; 
      this.createdTime = createdTime ;
    }
  }
  
  public void assertMultiSegmentIterator(Segment[] segments, Result[] results, boolean merge) throws IOException {
    List<Result> holder = new ArrayList<Result>() ;
    MultiSegmentIterator iterator = new MultiSegmentIterator(segments) ;
    
    iterator.next() ;
    RowId nextKey = iterator.currentKey() ;
    Cell nextValue = iterator.currentValue() ;
    
    RowId currentKey = nextKey;
    Cell currentValue = nextValue;
    if(!merge) {
      holder.add(new Result(Bytes.toString(currentKey.getKey()), currentValue.getFieldAsString("value"), currentKey.getCreatedTime())) ;
    }
    while(iterator.next()) {
      nextKey = iterator.currentKey() ;
      nextValue = iterator.currentValue() ;
      if(merge) {
        CellMerger merger = new CellMerger.LatestCellMerger() ;
        if(currentKey.compareTo(nextKey) > 0) {
          throw new RuntimeException("Key is not order") ;
        }
        if(currentKey.compareTo(nextKey) == 0) {
          currentKey = merger.merge(currentKey, nextKey) ;
          currentValue = merger.merge(currentValue, nextValue) ;
          nextKey = null ; nextValue = null ;
        } else {
          currentKey = nextKey ;
          currentValue = nextValue ;
        }
      } else {
        currentKey = nextKey ;
        currentValue = nextValue ;
      }
      
      holder.add(new Result(Bytes.toString(currentKey.getKey()), currentValue.getFieldAsString("value"), currentKey.getCreatedTime())) ;
    }
    
    
    for(int i = 0; i < results.length; i++) {
      Result sel = holder.get(i) ;
      Assert.assertNotNull(sel) ;
      Assert.assertEquals(results[i].key, sel.key) ;
      Assert.assertEquals(results[i].value, sel.value) ;
      
      Calendar cal1 = Calendar.getInstance() ;
      cal1.setTimeInMillis(results[i].createdTime) ;
      
      Calendar cal2 = Calendar.getInstance() ;
      cal2.setTimeInMillis(sel.createdTime) ;
      
      Assert.assertEquals(cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH)) ;
      Assert.assertEquals(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH)) ;
      Assert.assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR)) ;
    }
  }
  
  public void assertData(String[] keys, String prefix, Segment segment, int expected) throws IOException {
    Segment.Writer writer = segment.getWriter() ;
    List<String> holder = new ArrayList<String>() ;
    
    for(int i = 0; i < keys.length; i++) {
      holder.add(keys[i]) ;
      
      Calendar cal = Calendar.getInstance() ;
      if(prefix.equals("first")) cal.add(Calendar.DAY_OF_MONTH, -1) ;
      else if(prefix.equals("second")) cal.add(Calendar.DAY_OF_MONTH, -2) ;
      RowId id = new RowId(new Text(keys[i]), cal.getTimeInMillis(), cal.getTimeInMillis(), RowId.STORE_STATE) ;
      Cell value = new Cell() ;
      value.addField("value", prefix + " " + keys[i]) ;
      value.setKey(id) ;
      writer.append(id, value) ;
    }
    writer.close() ;
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
