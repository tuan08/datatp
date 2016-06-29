package net.datatp.storage.batchdb;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.junit.Test;

import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.MultiSegmentIterator;
import net.datatp.storage.batchdb.Segment;
import net.datatp.storage.batchdb.util.HDFSUtil;
import net.datatp.util.io.FileUtil;

public class SegmentNewUnitTest {
  private String dblocation = "target/db" ;

  @Test
  public void test() throws Exception {
    FileUtil.removeIfExist(dblocation, false) ;
    
    // Single Segment Unit Test
  	CellDatas strData = new CellDatas().
      input1("1", "1").input1("3", "3").input1("2", "2").input1("1", "1(update)").input1("1","").input1("","").
      expect("", "").expect("1", "1").expect("1", "1(update)").expect("1", "").expect("2", "2").expect("3", "3");
    assertData(strData) ;
  
    CellDatas intData = new CellDatas().
      input1(1, "1").input1(3, "3").input1(2, "2").input1(2, "2(update)").input1(2, "").
      expect(1, "1").expect(2, "2").expect(2, "2(update)").expect(2, "").expect(3, "3");
    assertData(intData) ;
    
    CellDatas longData = new CellDatas().
      input1(1L, "1").input1(3L, "3").input1(2L, "2").input1(4L, "4").input1(2L, "2(update1)").input1(2L, "2(update2)").
      expect(1L, "1").expect(2L, "2").expect(2L, "2(update1)").expect(2L, "2(update2)").expect(3L, "3").expect(4L, "4");
    assertData(longData);
    
    CellDatas textData = new CellDatas().
      input1(new Text("3"), "3").input1(new Text("2"), "2").input1(new Text("1"), "1").input1(new Text("1"), "1(update)").
      expect(new Text("1"), "1").expect(new Text("1"), "1(update)").expect(new Text("2"), "2").expect(new Text("3"), "3");
    assertData(textData);
  }
  
  private void assertData(CellDatas data) throws Exception {
    Segment segment =  null;

    if(data.getInput1().size() > 0){ 
      segment = new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(0)) ;
      data.appendInput(segment, data.getInput1()); 
    } else throw new Exception("Input is empty!");
    
    MultiSegmentIterator itr = new MultiSegmentIterator(new Segment[]{segment});
    List<Cell> cells = new ArrayList<Cell>() ;
    while(itr.next()) {
      Cell cell = itr.currentValue() ;
      cells.add(cell) ;
    }
    data.assertExpect(cells) ;
  }
}