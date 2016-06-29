package net.datatp.storage.batchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;

import junit.framework.Assert;
import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;
import net.datatp.storage.batchdb.ColumnDefinition;
import net.datatp.storage.batchdb.Database;
import net.datatp.storage.batchdb.Row;
import net.datatp.storage.batchdb.RowDB;
import net.datatp.storage.batchdb.RowId;

public class RowDatas {
  private List<RowData> input1 = new ArrayList<RowData>();
  private List<RowData> input2 = new ArrayList<RowData>();
  private List<RowData> input3 = new ArrayList<RowData>();
  
  private List<RowData> expects = new ArrayList<RowData>();
  
  public RowDatas input1(RowData rowData){
    input1.add(rowData);
    return this;
  }
  
  public RowDatas input2(RowData rowData){
    input2.add(rowData);
    return this;
  }
  
  public RowDatas input3(RowData rowData){
    input3.add(rowData);
    return this;
  }

  public RowDatas expect(RowData rowData){
    expects.add(rowData) ;
    return this;
  }
 
  public void appendInput(RowDB rowdb) throws IOException{
    if(input1.size() > 0) appendInput(rowdb, input1);
    if(input2.size() > 0) appendInput(rowdb, input2);
    if(input3.size() > 0) appendInput(rowdb, input3);
  }
  
  private void appendInput(RowDB rowdb, List<RowData> rows) throws IOException{
    RowDB.Writer writer = rowdb.getCurrentWriter();
    for(RowData row: rows){
      writer.write(row.getRowId(), row, null);
    }
    rowdb.closeCurrentWriter();
  }
  
  public void appendInput(Database db) throws IOException{
    if(input1.size() > 0) appendInput(db, input1);
    if(input2.size() > 0) appendInput(db, input2);
    if(input3.size() > 0) appendInput(db, input3);
  }
  
  private void appendInput(Database db, List<RowData> rows) throws IOException{
   Database.Writer writer = db.getWriter();
   for(RowData row: rows){
     writer.write(row.getRowId(), row, null);
   }
   writer.close();
  }
  
  public void assertRowdbExpect(RowDB rowdb, ColumnDefinition[] colDefinition) throws Exception{
    String[] colDefs = new String[colDefinition.length];
    for(int i = 0; i < colDefinition.length; i++) colDefs[i] = colDefinition[i].getName();

    RowDB.RowReader reader = rowdb.getRowReader(colDefs) ;
    Row actualRow = null;
    ArrayList<Row> actuals = new ArrayList<Row>();
    while((actualRow = reader.next()) != null) { actuals.add(actualRow); }
    reader.close();
    assertExpect(actuals, colDefs);
  }
  
  public void assertDbExpect(Database db) throws Exception{
    ColumnDefinition[] colDefinition = db.getDatabaseConfiguration().getColumnDefinition();
    String[] colDefs = new String[colDefinition.length];
    for(int i = 0; i < colDefinition.length; i++) colDefs[i] = colDefinition[i].getName();
    
    Database.Reader reader = db.getReader(colDefs);
    Row actualRow = null;
    ArrayList<Row> actuals = new ArrayList<Row>();
    while((actualRow = reader.next()) != null) { actuals.add(actualRow); }
    reader.close();
    assertExpect(actuals, colDefs);
  }

  private void assertExpect(List<Row> actuals, String[] colDefs) throws Exception{
    Assert.assertEquals(expects.size(), actuals.size()) ;
    for(int i = 0; i < expects.size(); i++){
      Row expect = expects.get(i);
      Row actual = actuals.get(i);
      Assert.assertEquals(Bytes.toString(expect.getRowId().getKey()), Bytes.toString(actual.getRowId().getKey()));
      for(String colDef: colDefs){ assertRowData(colDef, expect, actual); }
    }
  }
  
  private void assertRowData(String col, Row expectRow, Row actualRow) throws Exception{
    Cell cell1 = expectRow.get(col);
    Cell cell2 = actualRow.get(col);
    if(cell1 == null && cell2 != null) throw new Exception("Column " + col + " does not exist as expectation");
    else if(cell1 == null && cell2 == null) return;
    Assert.assertEquals(cell1.getName(), cell2.getName());
    Assert.assertEquals(cell1.getFieldAsString("value"), cell2.getFieldAsString("value"));
  }

  public void removeColumnDB(RowDB rowdb, String col) throws IOException{
    rowdb.removeColumnDB(col);
  }
  
  public void removeColumnDB(Database db, String col) throws IOException{
    db.removeColumnDB(col);
  }
  
  static public class RowData extends Row{
    private static final long serialVersionUID = 1L;
    
    private long ctime = System.currentTimeMillis() ;

    public RowData(String rowid){
        this.setRowId(new RowId(rowid, ctime, ctime, RowId.STORE_STATE));
    }
    public RowData(int rowid){
      this.setRowId(new RowId(rowid, ctime, ctime, RowId.STORE_STATE));
    }
    public RowData(long rowid){
      this.setRowId(new RowId(rowid, ctime, ctime, RowId.STORE_STATE));
    }
    public RowData(Text rowid){
      this.setRowId(new RowId(rowid, ctime, ctime, RowId.STORE_STATE));
    }
    public RowData addCell(String col, String value){
      Cell cell = new Cell();
      cell.setKey(getRowId());
      cell.setName(col);
      cell.addField("value", value);
      this.addCell(col, cell);
      return this;
    }
  }
  
}
