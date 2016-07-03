package net.datatp.storage.batchdb;

import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.ColumnFamilyDB;
import net.datatp.storage.batchdb.ColumnFamilyIterator;
import net.datatp.storage.batchdb.DatabaseConfiguration;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.Segment;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.io.FileUtil;
import net.datatp.util.text.DateUtil;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class ColumnFamilyDBUnitTest {
  static DecimalFormat NFORMATER = new DecimalFormat("0000000000000") ;
  
  @Test
  public void test() throws Exception {
    String dblocation = "target/db/partition-0" ;
    FileUtil.removeIfExist("target/db", false) ;
    ColumnDefinition columnDefinition = new ColumnDefinition("test") ;
    DatabaseConfiguration dbconfiguration = 
    	new DatabaseConfiguration(new ColumnDefinition[] {columnDefinition}, null) ;
    dbconfiguration.setHadoopConfiguration(HDFSUtil.getDaultConfiguration()) ;
    ColumnFamilyDB db = new ColumnFamilyDB(dblocation, dbconfiguration, columnDefinition) ;
    db.reload() ;
    
    Segment segment0 = db.newSegment() ;
    populateSegmentData(segment0, 10, 1) ;
  
    Segment segment1 = db.newSegment() ;
    populateSegmentData(segment1, 20, 2) ;
    assertData(db, 15) ;
    
    db.autoCompact(null) ;
    assertData(db, 15) ;
  }
  
  private void assertData(ColumnFamilyDB db, int expectSize) throws Exception {
    ColumnFamilyIterator mitr = db.getColumnFamilyIterator() ;
    RowId pkey = null, key = new RowId() ;
    Cell pvalue = null, value  = new Cell() ;
    
    int count = 0 ;
    while(mitr.next()) {
      key = mitr.currentKey() ;
      value = mitr.currentValue() ;
      if(pkey != null) {
        Assert.assertTrue(pkey.compareTo(key) < 0) ;
      }
      if(value.getFieldAsInt("value") % 2 == 0) {
        Assert.assertEquals(0, value.getFieldAsInt("segment")) ;
        Calendar cal = Calendar.getInstance() ;
        cal.add(Calendar.DAY_OF_MONTH, 0) ;
        Assert.assertEquals(DateUtil.asCompactDate(key.getCreatedTime()), DateUtil.asCompactDate(cal)) ;
      } else {
        Assert.assertEquals(1, value.getFieldAsInt("segment")) ;
        int v = value.getFieldAsInt("value") ;
        Calendar cal = Calendar.getInstance() ;
        if(v > 10) cal.add(Calendar.DAY_OF_MONTH, -1) ;
        else cal.add(Calendar.DAY_OF_MONTH, 0) ;
        Assert.assertEquals(DateUtil.asCompactDate(key.getCreatedTime()), DateUtil.asCompactDate(cal)) ;
      }
      pkey = key ; pvalue = value ;
      count++ ;
    }
    mitr.close() ;
    Assert.assertEquals(count, expectSize) ;
  }
  
  private void populateSegmentData(Segment seg, int size, int incr) throws Exception {
    Segment.Writer writer = seg.getWriter() ;
    Calendar cal = Calendar.getInstance() ;
    cal.add(Calendar.DAY_OF_MONTH, seg.getIndex() * -1) ;
    for(int i = seg.getIndex(); i < size; i += incr) {
      Cell datum = createCell(seg.getIndex(), i) ;
      RowId key = new RowId(new Text(datum.getFieldAsString("url")), cal.getTimeInMillis(), cal.getTimeInMillis(), RowId.STORE_STATE) ;
      writer.append(key, datum); 
    }
    writer.close() ;
  }
  
  private Cell createCell(int segment, int value) {
    String url = "http://vnexpress.net/" + NFORMATER.format(value) ;
  	Cell datum = new Cell() ;
    datum.addField("url", url) ;
    datum.addField("value", value);
    datum.addField("segment", segment) ;
    return datum ;
  }
}