package net.datatp.storage.kvdb;

import java.io.IOException;

import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.WritableComparable;

/**
 * Author : Tuan Nguyen
 *          tuan@gmail.com
 * Apr 20, 2010  
 */
abstract public class MultiSegmentIterator<K extends WritableComparable, V extends Record> {
  private SequenceFile.Reader[] reader = null ;
  private K[] key ;
  private V[] value ;
  private K currentKey = null ;
  private V currentValue = null ;
  
  public MultiSegmentIterator(Segment<K, V>[] segments) throws Exception {
    this.reader = new SequenceFile.Reader[segments.length] ;
    for(int i = 0; i < segments.length; i++) {
      this.reader[i] = segments[i].getReader() ;
    }
    key = (K[]) new WritableComparable[reader.length] ;
    value = (V[]) new Record[reader.length] ;
  }
  
  public K currentKey() { return currentKey ; }
  public V currentValue() { return this.currentValue ; }
  
  public boolean next() throws IOException {
    for(int i = 0; i < reader.length; i++) {
      if(key[i] == null) {
        if(reader[i] != null) {
          key[i] = createKey() ; value[i] = createValue() ;
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
  
  abstract public K createKey() ;
  abstract public V createValue() ;
}
