package net.datatp.storage.batchdb;

import java.util.HashMap;

public class Row extends HashMap<String, Cell> {
  private static final long serialVersionUID = 1L;
  
  private RowId rowId ;
  
  public RowId getRowId() { return this.rowId ; }
  public void  setRowId(RowId id) { this.rowId = id ; }
  
	public Cell getCell(String name) { return get(name) ; }
	
	public void addCell(String name, Cell cell) {
		put(name, cell) ;
	}
}
