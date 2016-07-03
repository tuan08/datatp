package net.datatp.storage.batchdb;

import org.junit.Test;

import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.DatabaseConfiguration;
import net.datatp.storage.batchdb.RowDB;
import net.datatp.storage.batchdb.RowIdPartitioner;
import net.datatp.storage.batchdb.RowDatas.RowData;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.io.FileUtil;

public class RowDBUnitNewTest {
  private String dblocation = "target/db" ;
  
  @Test
  public void test() throws Exception {
    RowDatas rowDatas = new RowDatas().
      // Insert single column / multicolumn row
      // Update single column of a row in same input
      input1(new RowData("r1").addCell("c1", "1")).
      input1(new RowData("r2").addCell("c1", "1").addCell("c2", "2")).
      input1(new RowData("r3").addCell("c1", "1").addCell("c2", "2").addCell("c3", "3")).
      input1(new RowData("r1").addCell("c1", "1(update1)")).
      // Insert a row without column
      // Insert single column
      // Update single column / multicolumn of a row in same input
      input2(new RowData("r4")).
      input2(new RowData("r2").addCell("c4", "4")). // Add c4 to test removing
      input2(new RowData("r1").addCell("c1", "1(update2)").addCell("c3", "3")).
      input2(new RowData("r3").addCell("c1", "1(update1)").addCell("c2", "2(update1)")).
      // Insert single column / multicolumn row
      // Update multicolumn of a row in different input
      input3(new RowData("r1").addCell("c2", "2")).
      input3(new RowData("r4").addCell("c1", "1").addCell("c2", "2")).
      input3(new RowData("r3").addCell("c3", "3(update1)")).
      input3(new RowData("r2").addCell("c1", "1(update1)").addCell("c2", "2(update1)")).
      // Expect data
      expect(new RowData("r1").addCell("c1", "1(update2)").addCell("c2", "2").addCell("c3", "3")).
      expect(new RowData("r2").addCell("c1", "1(update1)").addCell("c2", "2(update1)")).
      expect(new RowData("r3").addCell("c1", "1(update1)").addCell("c2", "2(update1)").addCell("c3", "3(update1)")).
      expect(new RowData("r4").addCell("c1", "1").addCell("c2", "2"));
    
    assertData(rowDatas);
  }
  
  public void assertData(RowDatas rowDatas) throws Exception{
    FileUtil.removeIfExist(dblocation, false) ;
    ColumnDefinition[] columnDefinition = {
      new ColumnDefinition("c1"), new ColumnDefinition("c2"), 
      new ColumnDefinition("c3"), new ColumnDefinition("c4")
    } ;
    DatabaseConfiguration dbconfiguration = 
      new DatabaseConfiguration(columnDefinition, new RowIdPartitioner.RowIdHashPartioner(3, ":")) ;
    dbconfiguration.setHadoopConfiguration(HDFSUtil.getDaultConfiguration()) ;
    RowDB rowDB = new RowDB(dblocation, "rowdb", dbconfiguration) ;
    
    rowDatas.appendInput(rowDB);
    rowDB.autoCompact(null);
    
    rowDatas.removeColumnDB(rowDB, "c4");
    
    rowDatas.assertRowdbExpect(rowDB, columnDefinition);
    rowDatas.assertRowdbExpect(rowDB, new ColumnDefinition[]{new ColumnDefinition("c1")});
  }

}
