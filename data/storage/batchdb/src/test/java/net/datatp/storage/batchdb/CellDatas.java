package net.datatp.storage.batchdb;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;

import junit.framework.Assert;
import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnFamilyDB;
import net.datatp.storage.batchdb.RowId;
import net.datatp.storage.batchdb.Segment;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CellDatas {
	private long   ctime = System.currentTimeMillis() ;
	private String columnName = "column" ;
	private List<Cell> input1 = new ArrayList<Cell>() ;
	private List<Cell> input2 = new ArrayList<Cell>() ;
	private List<Cell> input3 = new ArrayList<Cell>() ;
	
	private List<Cell> expect = new ArrayList<Cell>() ;
	
	public List<Cell> getInput1(){ return this.input1;}
	public List<Cell> getInput2(){ return this.input2;}
	public List<Cell> getInput3(){ return this.input3;}
	
 /// Input1
	public CellDatas input1(String key, String value) {
	  input1.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas input1(int key, String value) {
	  input1.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas input1(long key, String value) {
	  input1.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas input1(Text key, String value) {
	  input1.add(create(key, value)) ;
	  return this ;
	}
 /// Input 2
	public CellDatas input2(String key, String value) {
    input2.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input2(int key, String value) {
    input2.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input2(long key, String value) {
    input2.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input2(Text key, String value) {
    input2.add(create(key, value)) ;
    return this ;
  }
/// Input 3
  public CellDatas input3(String key, String value) {
    input3.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input3(int key, String value) {
    input3.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input3(long key, String value) {
    input3.add(create(key, value)) ;
    return this ;
  }
  public CellDatas input3(Text key, String value) {
    input3.add(create(key, value)) ;
    return this ;
  }
// Expect
	public CellDatas expect(String key, String value) {
	  expect.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas expect(int key, String value) {
	  expect.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas expect(long key, String value) {
	  expect.add(create(key, value)) ;
	  return this ;
	}
	public CellDatas expect(Text key, String value) {
	  expect.add(create(key, value)) ;
	  return this ;
	}

	public void appendInput(Segment segment) throws Exception {
		Segment.Writer writer = segment.getWriter() ;
		appendInput(writer, input1) ;
		appendInput(writer, input2) ;
		appendInput(writer, input3) ;
		writer.close() ;
	}
	
	void appendInput(Segment.Writer writer, List<Cell> cells) throws Exception {
		for(int i = 0; i < cells.size(); i++) {
			Cell cell = cells.get(i) ;
			writer.append(cell.getKey(), cell) ;
		}
	}

	public void appendInput(Segment segment, List<Cell> cells) throws Exception {
		Segment.Writer writer = segment.getWriter() ;
		appendInput(writer, cells) ;
		writer.close() ;
	}
	
 public void appendInput(ColumnFamilyDB db) throws Exception {
	 if(input1.size() > 0) appendInput(db.newSegment(), input1) ;
	 if(input2.size() > 0) appendInput(db.newSegment(), input2) ;
	 if(input3.size() > 0) appendInput(db.newSegment(), input3) ;
 }
 
	public void assertExpect(List<Cell> cells) {
		Assert.assertEquals(expect.size(), cells.size()) ;
		for(int i = 0; i < cells.size(); i++) {
			Cell cell1 = expect.get(i) ;
			Cell cell2 = cells.get(i) ;
			byte[] value1 = cell1.getFieldAsBytes("value") ;
			byte[] value2 = cell2.getFieldAsBytes("value") ;
			int compare = 
				Bytes.compareTo(value1, 0, value1.length, value2, 0, value2.length) ;
			Assert.assertEquals(0, compare);
		}
	}
	
	private Cell create(String key, String value) {
		Cell cell = new Cell() ;
		cell.setKey(new RowId(key, ctime, ctime, RowId.STORE_STATE)) ;
		cell.setName("String "+ columnName) ;
		cell.addField("value", value) ;
		return cell ;
	}
	
	private Cell create(long key, String value) {
		Cell cell = new Cell() ;
		cell.setKey(new RowId(key, ctime, ctime, RowId.STORE_STATE)) ;
		cell.setName("Long" + columnName) ;
		cell.addField("value", value) ;
		return cell ;
	}
	
	private Cell create(int key, String value) {
		Cell cell = new Cell() ;
		cell.setKey(new RowId(key, ctime, ctime, RowId.STORE_STATE)) ;
		cell.setName("Int " + columnName) ;
		cell.addField("value", value) ;
		return cell ;
	}
	
	private Cell create(Text key, String value){
	  Cell cell = new Cell();
	  cell.setKey(new RowId(key, ctime, ctime, RowId.STORE_STATE));
	  cell.setName("Text " + columnName);
	  cell.addField("value", value);
	  return cell;
	}
}