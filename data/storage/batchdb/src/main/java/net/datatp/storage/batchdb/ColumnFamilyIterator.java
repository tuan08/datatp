package net.datatp.storage.batchdb;

import java.io.IOException;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 20, 2010  
 */
public class ColumnFamilyIterator {
  private MultiSegmentIterator iterator ;
  private CellMerger merger ;
  
  private RowId currentRowId, nextRowId ;
  private Cell currentCell, nextCell ;
  
  public ColumnFamilyIterator(MultiSegmentIterator iterator, CellMerger merger) {
    this.iterator = iterator ;
    this.merger = merger ;
  }
  
  public RowId currentKey() { return currentRowId ; }
  
  public Cell currentValue() { return currentCell ; }
  
  public boolean next() throws IOException {
    if(currentRowId == null && nextRowId == null) {
      if(!iterator.next()) return false ;
      nextRowId = iterator.currentKey() ;
      nextCell = iterator.currentValue() ;
    }
    currentRowId = nextRowId ; currentCell = nextCell ;
    nextRowId = null ; nextCell = null ;
    while(iterator.next()) {
      nextRowId = iterator.currentKey() ;
      nextCell = iterator.currentValue() ;
      if(currentRowId.compareTo(nextRowId) > 0) {
        String mesg = "RowId is not in the order. current id = " + currentRowId + ", nextId = " + nextRowId ;
        throw new IOException(mesg) ;
      } else if(currentRowId.compareTo(nextRowId) == 0) {
        //merge 
        currentRowId = merger.merge(currentRowId, nextRowId) ; 
        currentCell = merger.merge(currentCell, nextCell) ;
        nextRowId = null ; nextCell = null ;
      } else {
        break ;
      }
    }
    if(currentRowId != null && currentCell != null) return true ;
    return false ;
  }
  
  public void close() throws IOException {
    iterator.close() ;
  }
}
