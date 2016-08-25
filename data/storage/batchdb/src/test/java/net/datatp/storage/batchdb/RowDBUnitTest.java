package net.datatp.storage.batchdb;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.DatabaseConfiguration;
import net.datatp.storage.batchdb.Row;
import net.datatp.storage.batchdb.RowDB;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.RowIdPartitioner;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.URLAnalyzer;
import net.datatp.util.io.FileUtil;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class RowDBUnitTest {
  static DecimalFormat NFORMATER = new DecimalFormat("0000000000000") ;
  private Set<String> keyHolder = new HashSet<String>() ;
  
  @Test
  public void test() throws Exception {
    String dblocation = "target/db" ;
    FileUtil.removeIfExist(dblocation, false) ;
    ColumnDefinition[] columnDefinition = {
      new ColumnDefinition("column1"), new ColumnDefinition("column2")
    } ;
    DatabaseConfiguration dbconfiguration = 
    	new DatabaseConfiguration(columnDefinition, new RowIdPartitioner.RowIdHashPartioner(3, ":")) ;
    dbconfiguration.setHadoopConfiguration(HDFSUtil.getDaultConfiguration()) ;
    RowDB rowDB = new RowDB(dblocation, "rowdb", dbconfiguration) ;
    
    writeSegment(rowDB, 0) ;
    assertRowDB(rowDB, 0) ;
    
    writeSegment(rowDB, 1) ;
    assertRowDB(rowDB, 1) ;

    rowDB.autoCompact(null) ;
    assertRowDB(rowDB, 1) ;
  }
  
  private void assertRowDB(RowDB rowdb, int segment) throws Exception {
  	RowDB.RowReader reader = rowdb.getRowReader() ;
    Row row = null ;
    int count = 0 ;
    List<String> holder = new ArrayList<String>(keyHolder) ;
    Collections.sort(holder) ;
    while((row = reader.next()) != null) {
    	assertRow(row, holder.get(count),count, segment) ;
    	count++ ;
    }
    Assert.assertEquals(10, count) ;
    reader.close() ;
    
    reader = rowdb.getRowReader(new String[] { "column1" }) ;
    count = 0 ;
    while((row = reader.next()) != null) {
    	assertRowCells(row, new String[] { "column1" }) ;
    	count++ ;
    }
    Assert.assertEquals(10, count) ;
    reader.close() ;
  }
  
  private void assertRow(Row row, String key, int id, int segment) {
    Assert.assertNotNull(row.getRowId()) ;
    Assert.assertEquals(key, Bytes.toString(row.getRowId().getKey())) ;
    
  	Cell cell1 = row.get("column1") ;
  	Assert.assertNotNull(cell1) ;
  	Assert.assertEquals("column1", cell1.getName()) ;
  	Assert.assertNotNull(cell1.getKey()) ;
  	Assert.assertEquals("column 1", cell1.getFieldAsString("column")) ;
  	Assert.assertEquals(segment, cell1.getFieldAsInt("segment")) ;
  	
  	Cell cell2 = row.get("column2") ;
  	Assert.assertNotNull(cell2) ;
  	Assert.assertEquals("column2", cell2.getName()) ;
  	Assert.assertNotNull(cell2.getKey()) ;
  	Assert.assertEquals("column 2", cell2.getFieldAsString("column")) ;
  	Assert.assertEquals(segment, cell2.getFieldAsInt("segment")) ;
  }

  private void assertRowCells(Row row, String[] column) {
  	Assert.assertEquals(column.length, row.size()) ;
  	for(int i = 0; i < column.length; i++) {
  	  Cell cell = row.get(column[i]) ;
  	  Assert.assertNotNull(cell) ;
  	  Assert.assertNotNull(row.getRowId()) ;
  	}
  }
  
  private void writeSegment(RowDB rowdb, int segment) throws Exception {
  	RowDB.Writer writer = rowdb.getCurrentWriter() ;
    for(int i = 0;  i < 10; i++) {
    	Row row = createRow(i, segment) ;
    	writer.write(row.getRowId(), row, null) ;
    }
    rowdb.closeCurrentWriter() ;
  }
  
  private Row createRow(int id, int segment) {
  	String url = null ;
  	if(id % 3 == 0) url = "http://vnexpress.net/" + NFORMATER.format(id) ;
  	else if(id % 3 == 1) url = "http://vietnam.net/" + NFORMATER.format(id) ;
  	else  url = "http://dantri.com.vn/" + NFORMATER.format(id) ;
  	Cell cell1 = new Cell() ;
  	cell1.setName("cell1") ;
    cell1.addField("column", "column 1") ;
    cell1.addField("id", id) ;
    cell1.addField("segment", segment) ;
    
    Cell cell2 = new Cell() ;
    cell2.setName("cell2") ;
    cell2.addField("column", "column 2") ;
    cell2.addField("id", id) ;
    cell2.addField("segment", segment) ;
    
    Row row = new Row() ;
    URLAnalyzer urlnorm = new URLAnalyzer(url) ;
    keyHolder.add(urlnorm.getHostMD5Id()) ;
    row.setRowId(new RowId(new Text(urlnorm.getHostMD5Id()), 0L, 0L, RowId.STORE_STATE)) ;
    row.addCell("column1", cell1) ;
    row.addCell("column2", cell2) ;
    return row ;
  }
}