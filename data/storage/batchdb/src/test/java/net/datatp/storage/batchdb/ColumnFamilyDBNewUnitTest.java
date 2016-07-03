package net.datatp.storage.batchdb;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.ColumnFamilyDB;
import net.datatp.storage.batchdb.ColumnFamilyIterator;
import net.datatp.storage.batchdb.DatabaseConfiguration;
import net.datatp.storage.hdfs.HDFSUtil;
import net.datatp.util.io.FileUtil;

public class ColumnFamilyDBNewUnitTest {
  private String dblocation = "target/db" ;
  
  @Test
  public void test() throws Exception{
    CellDatas data = new CellDatas().
      input1("1", "1").input1("1", "1(update1)").input1("2", "2").input1("3", "3").
      input2("1", "1(update2)").input2("2", "2(update1)").input2("2", "2(update2)").
      input3("1", "1(update3)").input3("3", "3(update1)").input3("4", "4").input3("4", "4(update1)").
      expect("1", "1(update3)").expect("2", "2(update2)").expect("3", "3(update1)").expect("4", "4(update1)");
    assertData(data, true);
    assertData(data, false);
  }
  
  private void assertData(CellDatas data, boolean autoCompact) throws Exception {
    FileUtil.removeIfExist(dblocation, false) ;
    ColumnDefinition columnDefinition = new ColumnDefinition("Test") ;
    DatabaseConfiguration dbconfiguration = 
      new DatabaseConfiguration(new ColumnDefinition[] {columnDefinition}, null) ;
    dbconfiguration.setHadoopConfiguration(HDFSUtil.getDaultConfiguration()) ;
    ColumnFamilyDB db = new ColumnFamilyDB(dblocation, dbconfiguration, columnDefinition) ;

    data.appendInput(db);

    if(autoCompact) db.autoCompact(null);

    ColumnFamilyIterator itr = db.getColumnFamilyIterator() ;
    List<Cell> cells = new ArrayList<Cell>() ;
    while(itr.next()) {
      Cell cell = itr.currentValue() ;
      cells.add(cell) ;
    }
    itr.close();

    data.assertExpect(cells) ;
  }
}