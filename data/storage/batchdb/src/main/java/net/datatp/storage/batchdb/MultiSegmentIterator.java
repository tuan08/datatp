package net.datatp.storage.batchdb;

import java.io.IOException;

import org.apache.hadoop.io.SequenceFile;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 20, 2010  
 */
public class MultiSegmentIterator {
  private SequenceFile.Reader[] reader = null ;
  private RowId[] key ;
  private Cell[] value ;
  private RowId currentKey = null ;
  private Cell currentValue = null ;
  
  public MultiSegmentIterator(Segment[] segments) throws IOException {
    this.reader = new SequenceFile.Reader[segments.length] ;
    for(int i = 0; i < segments.length; i++) {
      this.reader[i] = segments[i].getReader() ;
    }
    key = new RowId[reader.length] ;
    value = new Cell[reader.length] ;
  }
  
  public RowId currentKey() { return currentKey ; }
  public Cell currentValue() { return this.currentValue ; }
  
  public boolean next() throws IOException {
    for(int i = 0; i < reader.length; i++) {
      if(key[i] == null) {
        if(reader[i] != null) {
          key[i] = new RowId() ; value[i] = new Cell() ;
          if(!reader[i].next(key[i], value[i])) {
            key[i] = null ; value[i] = null ;
            reader[i].close() ;
            reader[i] = null ;
          }
        }
      }
    }
    currentKey = null ;
    currentValue = null ;
    int selectPos = -1 ; 
    for(int i = 0; i < key.length; i++) {
      if(selectPos < 0 && key[i] != null) {
        selectPos = i ;
      } else if(key[i] != null) {
        if(key[i].compareTo(key[selectPos]) < 0) selectPos = i ; 
      }
    }
    if(selectPos >=0 ) {
      currentKey = key[selectPos] ;
      currentValue = value[selectPos] ;
      key[selectPos] = null ;value[selectPos] = null ;
      return true ;
    }
    return false ;
  }
  
  public void close() throws IOException {
    for(int i = 0; i < reader.length; i++) {
      if(reader[i] != null) reader[i].close() ;
    }
  }
}
