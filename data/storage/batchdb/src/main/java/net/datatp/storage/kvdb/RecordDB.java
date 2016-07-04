package net.datatp.storage.kvdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.WritableComparable;

import net.datatp.storage.hdfs.SortKeyValueFile;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 */
abstract public class RecordDB<K extends WritableComparable<?>, V extends Record> {
  private Configuration configuration ;
  private String dblocation ;
  private Class<K> keyType ;
  private Class<V> valueType ;
  private Segment<K,V>[] segments ;

  public RecordDB() {
  }
  
  public RecordDB(Configuration configuration, String dblocation, Class<K> keyType, Class<V> valueType) throws Exception {
    onInit(configuration, dblocation, keyType,valueType) ;
  }
  
  public void onInit(Configuration configuration, String dblocation, Class<K> keyType, Class<V> valueType) throws Exception {
    this.configuration = configuration ;
    this.dblocation = dblocation ;
    this.keyType = keyType ;
    this.valueType = valueType ;
  }
  
  public Configuration getConfiguration() { return this.configuration ; }
  
  public String getDbLocation() { return this.dblocation ; }
  public void   setDbLocation(String location) { this.dblocation = location ; }
  
  synchronized public void reload() throws Exception {
    FileSystem fs = FileSystem.get(configuration) ;
    Path dbLocationPath = new Path(dblocation);
    if(!fs.exists(dbLocationPath)) fs.mkdirs(dbLocationPath);
    FileStatus[] status = fs.listStatus(dbLocationPath) ;
    List<Segment<K, V>> holder = new ArrayList<Segment<K,V>>() ;
    for(int i = 0; i < status.length; i++) {
      Path path = status[i].getPath() ;
      if(!path.getName().startsWith("segment")) continue ;
      holder.add(new Segment<K,V>(configuration, dblocation, path.getName(), keyType, valueType)) ;
    }
    Collections.sort(holder) ;
    this.segments = holder.toArray(new Segment[holder.size()]) ;
  }
  
  synchronized public Segment<K,V>[] getSegments() { return segments ; }
  
  synchronized public Segment<K,V> newSegment() throws Exception {
    Segment<K,V>[] temp = new Segment[segments.length + 1] ;
    for(int i = 0; i < segments.length; i++) {
      temp[i] = segments[i] ;
    }
    String segmentName = null ;
    if(segments.length == 0) {
      segmentName = Segment.createSegmentName(0) ;
    } else {
      segmentName = Segment.createSegmentName(segments[segments.length - 1].getIndex() + 1) ;
    }
    temp[segments.length] = new Segment<K,V>(configuration, dblocation, segmentName, keyType, valueType) ;
    this.segments = temp ;
    return this.segments[segments.length - 1] ;
  }
 
  synchronized public MultiSegmentIterator<K, V> getDatumIterator() throws Exception {
    final RecordDB<K, V> db = this; 
    MultiSegmentIterator<K, V> iterator = new MultiSegmentIterator<K, V>(this.segments) {
      public K createKey() { return db.createKey() ; }
      public V createValue() { return db.createValue() ; }
    };
    return iterator ;
  }
  
  public MergeMultiSegmentIterator<K, V> getMergeRecordIterator() throws Exception {
    return new MergeMultiSegmentIterator<K, V>(getDatumIterator(), createRecordMerger()) ;
  }
  
  synchronized MergeMultiSegmentIterator<K, V> getMergeRecordIterator(RecordMerger<V> merger) throws Exception {
    return new MergeMultiSegmentIterator<K, V>(getDatumIterator(), merger) ;
  }

  synchronized public void autoCompact() throws Exception {
    if(this.segments.length == 1) return ;
    Segment<K, V> psegment = null ;
    List<Segment<K, V>> mergeSegments = new ArrayList<Segment<K, V>>() ;
    for(int i = 0; i < segments.length; i++) {
      if(psegment != null) {
        long dataSize = segments[i].getDataSize() ;
        if(dataSize > psegment.getDataSize()/2) {
          mergeSegments.add(psegment) ;
          for(int j = i; j < segments.length; j++) {
            mergeSegments.add(segments[j]) ;
          }
          break ;
        }
      }
      psegment = segments[i] ;
    }
    if(mergeSegments.size() < 1) return  ;
    Segment<K, V>[] msegments = mergeSegments.toArray(new Segment[mergeSegments.size()]) ;
    final RecordDB<K, V> db = this; 
    MultiSegmentIterator<K, V> iterator = new MultiSegmentIterator<K, V>(msegments) {
      public K createKey() { return db.createKey() ; }
      public V createValue() { return db.createValue() ; }
    };
    MergeMultiSegmentIterator<K, V> mitr = 
    	new MergeMultiSegmentIterator<K, V>(iterator, createRecordMerger()) ;
    SortKeyValueFile<K, V>.Writer writer = msegments[0].getWriter() ;
    while(mitr.next()) {
      K key = mitr.currentKey() ;
      V value = mitr.currentValue() ;
      writer.append(key, value) ;
    }
    mitr.close() ;
    writer.close() ;
    for(int i = 1; i < msegments.length; i++) {
      msegments[i].delete() ;
    }
    reload() ;
  }
  
  synchronized public void update(RecordUpdater<V> updater) throws Exception {
    if(this.segments.length == 0) return ;
    Segment<K, V>[] msegments = this.segments ;
    final RecordDB<K, V> db = this; 
    MultiSegmentIterator<K, V> iterator = new MultiSegmentIterator<K, V>(msegments) {
      public K createKey() { return db.createKey() ; }
      public V createValue() { return db.createValue() ; }
    };
    MergeMultiSegmentIterator<K, V> mitr = 
    	new MergeMultiSegmentIterator<K, V>(iterator, createRecordMerger()) ;
    SortKeyValueFile<K, V>.Writer writer = msegments[0].getWriter() ;
    while(mitr.next()) {
      K key = mitr.currentKey() ;
      V value = mitr.currentValue() ;
      value = updater.update(key, value) ;
      if(value != null) writer.append(key, value) ;
    }
    mitr.close() ;
    writer.close() ;
    for(int i = 1; i < msegments.length; i++) {
      msegments[i].delete() ;
    }
    reload() ;
  }
  
  protected RecordMerger<V> createRecordMerger() { return new RecordMerger.LatestRecordMerger<V>() ; }
  
  abstract public K createKey() ;
  abstract public V createValue() ;
}