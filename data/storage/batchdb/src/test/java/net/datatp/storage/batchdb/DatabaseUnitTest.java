package net.datatp.storage.batchdb;

import java.io.IOException;
import java.text.DecimalFormat;

import org.junit.Assert;

import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.Database;
import net.datatp.storage.batchdb.DatabaseConfiguration;
import net.datatp.storage.batchdb.Row;
import net.datatp.storage.batchdb.RowDB;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.RowIdPartitioner;
import net.datatp.storage.batchdb.RowDB.RowReader;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.URLParser;
import net.datatp.util.io.FileUtil;

import org.apache.hadoop.io.Text;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class DatabaseUnitTest {
  static DecimalFormat NFORMATER = new DecimalFormat("0000000000000") ;
  
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
   
    Database database = Database.getDatabase(dblocation, dbconfiguration) ;
    
    Database.Writer writer = database.getWriter() ;
    for(int i = 0;  i < 10; i++) {
    	Row row = createRow(i) ;
    	writer.write(row.getRowId(), row, null) ;
    }
    writer.close() ;
    Database.Reader reader = database.getReader() ;
    Row row = null ;
    int count  = 0 ;
    while((row = reader.next()) != null) {
      assertRow(row, database) ;
    	System.out.println(count + ". " + Bytes.toString(row.getRowId().getKey())) ;
    	count++ ;
    }
  }
  
  private void assertRow(Row row, Database database) throws IOException {
    RowId id = row.getRowId() ;
    byte[] key1 = id.getKey() ;
    int numberPartition = database.getRowIdPartitioner().getPartition(id) ;
    RowDB rowDB = database.getRowDB(numberPartition) ;
    RowReader reader = rowDB.getRowReader(new String[] {}) ;
    int count = 0 ;
    row = null ;
    while((row = reader.next()) != null) {
      byte[] key2 = row.getRowId().getKey() ;
      if(Bytes.compareTo(key1, 0, key1.length, key2, 0, key2.length) == 0) count++ ;
    }
    Assert.assertEquals(1, count) ;
  }
  
  
  private Row createRow(int id) {
  	String url = null ;
  	if(id % 3 == 0) url = "http://vnexpress.net/" + NFORMATER.format(id) ;
  	else if(id % 3 == 1) url = "http://vietnam.net/" + NFORMATER.format(id) ;
  	else  url = "http://dantri.com.vn/" + NFORMATER.format(id) ;
  	Cell cell1 = new Cell() ;
    cell1.addField("column1", "column 1") ;
    cell1.addField("id", id) ;
    
    Cell cell2 = new Cell() ;
    cell2.addField("column2", "column 2") ;
    cell2.addField("id", id) ;
    
    Row row = new Row() ;
    URLParser urlnorm = new URLParser(url) ;
    row.setRowId(new RowId(new Text(urlnorm.getHostMD5Id()), 0L, 0L, RowId.STORE_STATE)) ;
    row.addCell("column1", cell1) ;
    row.addCell("column2", cell2) ;
    return row ;
  }
}