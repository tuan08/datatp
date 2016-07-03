package net.datatp.storage.batchdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;

import net.datatp.storage.hdfs.HDFSUtil;

public class RowDB {
  private String name ;
  private int    partitionId ;
  private DatabaseConfiguration dbconfiguration ;
  private String location ;
  private ColumnFamilyDB rowIdDb ;
  private Map<String, ColumnFamilyDB> columndbs ;
  private Writer currentWriter ;

  public RowDB(String location, String name, DatabaseConfiguration dbconfiguration) throws IOException {
    this.location = location ;
    this.name = name ;
    this.dbconfiguration = dbconfiguration ;
    ColumnDefinition rowIdDef = new ColumnDefinition("rowid") ;
    this.rowIdDb = new ColumnFamilyDB(getRowDBLocation(), dbconfiguration, rowIdDef) ;
    columndbs = new HashMap<String, ColumnFamilyDB>() ;
  }

  public String getName() { return this.name ; }

  public int getPartitionId() { return this.partitionId ; }
  public void setPartitionId(int id) { this.partitionId = id ; }

  public String getRowDBLocation() { return location + "/" + name ; }

  public RowReader getRowReader() throws IOException { 
    return new RowReader(dbconfiguration.getColumnDefinition()) ; 
  }

  public RowReader getRowReader(String[] column) throws IOException { 
    ColumnDefinition[] colDef = null ;
    if(column == null) {
    	colDef = dbconfiguration.getColumnDefinition() ;
    } else {
    	colDef = new ColumnDefinition[column.length] ;
    	for(int i = 0; i < colDef.length; i++) {
    		colDef[i] = dbconfiguration.getColumnDefinition(column[i]) ;
    		if(colDef[i] == null) {
    			throw new IOException("Database does not have the column " + column[i]) ;
    		}
    	}
    }
    return new RowReader(colDef) ; 
  }

  public Writer getCurrentWriter() throws IOException {
    if(currentWriter == null) currentWriter = new Writer() ;
    return currentWriter ;
  }

  public void closeCurrentWriter() throws IOException {
    if(currentWriter == null) return ;
    currentWriter.close() ;
    currentWriter = null  ;
  }

  public void autoCompact(Reporter reporter) throws IOException {
    this.rowIdDb.autoCompact(reporter) ;
    ColumnDefinition[] colDef = dbconfiguration.getColumnDefinition() ;
    for(int i = 0; i < colDef.length; i++) {
      ColumnFamilyDB columndb = getColumnFamilyDB(colDef[i].getName()) ;
      columndb.autoCompact(reporter) ;
    }
  }

  private ColumnFamilyDB getColumnFamilyDB(String name) throws IOException {
    ColumnFamilyDB column = columndbs.get(name) ;
    if(column == null) {
      ColumnDefinition def = dbconfiguration.getColumnDefinition(name) ;
      column = new ColumnFamilyDB(getRowDBLocation(), dbconfiguration, def) ;
      columndbs.put(name, column) ;
    }
    return column ;
  }
  
  public void removeColumnDB(String name) throws IOException {
    ColumnFamilyDB column = getColumnFamilyDB(name) ;
    if(column != null) {
    	FileSystem fs = FileSystem.get(dbconfiguration.getHadoopConfiguration()) ;
    	HDFSUtil.removeIfExists(fs, column.getLocation()) ;
      columndbs.remove(name) ;
    }
  }

  public class RowReader {
    private ColumnFamilyIterator idIteratpor ;
    private ColumnDefinition[] colDef ;
    private ColumnFamilyIterator[] colIterator ;
    private RowId[] colRowId ;
    private Cell[]  colCell ;
    private RowId previousRowId ;

    private RowReader(ColumnDefinition[] colDef) throws IOException {
      this.idIteratpor = rowIdDb.getColumnFamilyIterator() ;
      this.colDef = colDef ;
      this.colIterator = new ColumnFamilyIterator[colDef.length] ;
      for(int i = 0; i < colIterator.length; i++) {
        colIterator[i] = getColumnFamilyDB(colDef[i].getName()).getColumnFamilyIterator() ;
      }
      this.colRowId = new RowId[colIterator.length] ;
      this.colCell  = new Cell[colIterator.length] ;
    }

    public Row next() throws IOException {
      if(idIteratpor.next()) {
        RowId rowId = this.idIteratpor.currentKey() ;
        Row row = new Row() ;
        row.setRowId(rowId) ;
        for(int i = 0; i < colIterator.length; i++) {
          if(this.colRowId[i] == null && this.colIterator[i].next()) {
            this.colRowId[i] = this.colIterator[i].currentKey() ;
            this.colCell[i]  = this.colIterator[i].currentValue() ;
            this.colCell[i].setName(colDef[i].getName()) ;
            this.colCell[i].setKey(this.colRowId[i]) ;
          }
        }
        for(int i = 0 ; i < colRowId.length; i++) {
        	//TODO: review this
        	if(colRowId[i] == null) continue ;
          int compare = rowId.compareTo(colRowId[i]) ;
          if(compare == 0) {
            row.addCell(colCell[i].getName(), colCell[i]) ;
            this.colCell[i] = null ;
            this.colRowId[i] = null ;
          } else if(compare > 0) {
            throw new IOException("row id " + Bytes.toString(rowId.getKey()) + " col cell " + Bytes.toString(colRowId[i].getKey()) + " is out of order!") ;
          }
        }
        if(previousRowId != null) {
          if(previousRowId.compareTo(rowId) > 0) {
            throw new IOException("row id is out of the order") ;
          }
        }
        this.previousRowId = rowId ;
        return row ;
      }
      return null  ;
    }

    public void close() throws IOException {
      this.idIteratpor.close() ;
      for(int i = 0; i < colIterator.length; i++) {
        colIterator[i].close() ;
      }
    }
  }

  public class Writer {
    private Map<String, Segment.Writer> writers = new HashMap<String, Segment.Writer>() ;
    private Segment.Writer rowIdWriter ;

    public void write(RowId rowId, Row row, Reporter reporter) throws IOException {
      //			rowId.setCreatedTime(System.currentTimeMillis()) ;
      if(rowIdWriter == null) rowIdWriter = rowIdDb.newSegment().getWriter() ;
      rowIdWriter.append(rowId, Cell.NULL_CELL) ;

      Iterator<Map.Entry<String, Cell>> i = row.entrySet().iterator() ;
      while(i.hasNext()) {
        Map.Entry<String, Cell> entry = i.next() ;
        Segment.Writer writer = getWriter(entry.getKey()) ;
        writer.append(rowId, entry.getValue()) ;
        if(reporter != null) reporter.increment(entry.getValue().getName(), 1) ;
      }
      if(reporter != null) reporter.increment("Row", 1) ;
    }

    public void close() throws IOException {
      if(rowIdWriter != null) rowIdWriter.close() ;
      Iterator<Segment.Writer> i = writers.values().iterator();
      while(i.hasNext()) i.next().close() ;
      writers = null ;
    }

    private Segment.Writer getWriter(String column) throws IOException {
      Segment.Writer writer = writers.get(column) ;
      if(writer == null) {
        ColumnFamilyDB columndb = getColumnFamilyDB(column) ;
        writer = columndb.newSegment().getWriter() ;
        writers.put(column, writer) ;
      }
      return writer ;
    }
  }
}