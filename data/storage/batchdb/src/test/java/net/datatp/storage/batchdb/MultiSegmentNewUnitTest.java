package net.datatp.storage.batchdb;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.MultiSegmentIterator;
import net.datatp.storage.batchdb.Segment;
import net.datatp.storage.hdfs.HDFSUtil;

public class MultiSegmentNewUnitTest {
  private String dblocation = "target/db" ;
  
  @Test 
  public void test() throws Exception{
    
    CellDatas data = new CellDatas().
      input1("1", "1").input1("1", "1(update1)").input1("2", "2").
      input2("2", "2(update1)").input2("1", "1(update2)").input2("1", "1(update3)").
      input3("2", "2(update2)").
      expect("1", "1").expect("1", "1(update1)").expect("1", "1(update2)").expect("1", "1(update3)").
      expect("2", "2").expect("2", "2(update1)").expect("2", "2(update2)");
    
    assertMultiSegment(data);
  }
  
  private void assertMultiSegment(CellDatas data) throws Exception{
    List<Segment> segments = new ArrayList<Segment>();

    if(data.getInput1().size() > 0){
      Segment segment =  
        new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(segments.size())) ;
      data.appendInput(segment, data.getInput1());
      segments.add(segment);
    }
    if(data.getInput2().size() > 0){
      Segment segment =  
        new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(segments.size())) ;
      data.appendInput(segment, data.getInput2());
      segments.add(segment);
    }
    if(data.getInput3().size() > 0){
      Segment segment =  
        new Segment(HDFSUtil.getDaultConfiguration(), dblocation, Segment.createSegmentName(segments.size())) ;
      data.appendInput(segment, data.getInput3());
      segments.add(segment);
    }
   
    if(segments.size() <= 0) throw new Exception("No segment to write");
    MultiSegmentIterator itr = new MultiSegmentIterator(segments.toArray(new Segment[segments.size()]));
    List<Cell> cells = new ArrayList<Cell>();
    while(itr.next()) cells.add(itr.currentValue());

    itr.close();
    data.assertExpect(cells);
  }

}
