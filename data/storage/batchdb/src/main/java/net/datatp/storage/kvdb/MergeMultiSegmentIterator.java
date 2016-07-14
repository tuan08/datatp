package net.datatp.storage.kvdb;

import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * Author: Tuan Nguyen
 * Email:  tuan08@gmail.com
 */
public class MergeMultiSegmentIterator <K extends WritableComparable, V extends Record>{
  private MultiSegmentIterator<K, V> iterator ;
  private RecordMerger<V> merger ;
  
  private K currentKey,   nextKey ;
  private V currentValue, nextValue ;
  
  public MergeMultiSegmentIterator(MultiSegmentIterator<K, V> iterator, RecordMerger<V> merger) {
    this.iterator = iterator ;
    this.merger = merger ;
  }
  
  public K currentKey() { return currentKey ; }
  
  public V currentValue() { return currentValue ; }
  
  public boolean next() throws IOException {
    if(currentKey == null && nextKey == null) {
      if(!iterator.next()) return false ;
      nextKey = iterator.currentKey() ;
      nextValue = iterator.currentValue() ;
    }
    currentKey = nextKey ; currentValue = nextValue ;
    nextKey = null ; nextValue = null ;
    while(iterator.next()) {
      nextKey = iterator.currentKey() ;
      nextValue = iterator.currentValue() ;
      if(currentKey.compareTo(nextKey) > 0) {
        String mesg = "Key is not in the order. currentKey = " + currentKey + ", nextKey = " + nextKey ;
        throw new IOException(mesg) ;
      } else if(currentKey.compareTo(nextKey) == 0) {
        //merge 
        currentKey = nextKey ; currentValue = merger.merge(currentValue, nextValue) ;
        nextKey = null ; nextValue = null ;
      } else {
        break ;
      }
    }
    if(currentKey != null && currentValue != null) return true ;
    return false ;
  }
  
  public void close() throws IOException {
    iterator.close() ;
  }
}
