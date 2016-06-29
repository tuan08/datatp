package net.datatp.storage.batchdb;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 20, 2010  
 */
public interface CellMerger {
  public Cell merge(Cell c1, Cell c2) ;
  public RowId merge(RowId r1, RowId r2) ;

  static public class LatestCellMerger implements CellMerger {
    public Cell merge(Cell c1, Cell c2) { return c2; }
    public RowId merge(RowId r1, RowId r2) {
      r2.setCreatedTime(r1.getCreatedTime()) ;
      return r2 ;
    }
  }
}
