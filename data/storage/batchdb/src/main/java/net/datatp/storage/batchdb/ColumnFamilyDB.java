package net.datatp.storage.batchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class ColumnFamilyDB {
  final static public Segment[] NO_SEGMENT = {} ;

  private String location ;
  private DatabaseConfiguration dbconfiguration ;
  private ColumnDefinition columnDef ;
  private Segment[] segments = NO_SEGMENT ;

  public ColumnFamilyDB(String location, DatabaseConfiguration dbconfiguration, ColumnDefinition columnDef) throws IOException {
    this.location = location + "/" + columnDef.getName();
    this.dbconfiguration = dbconfiguration ;
    this.columnDef = columnDef ;
    reload() ;
  }

  public String getLocation() { return location ; }

  public ColumnDefinition getColumnDefinition() { return this.columnDef ; }

  synchronized public void reload() throws IOException {
    FileSystem fs = FileSystem.get(dbconfiguration.getHadoopConfiguration()) ;
    List<Segment> holder = new ArrayList<Segment>() ;
    Path locationPath = new Path(location);
    if(fs.exists(locationPath)) {
      FileStatus[] status = fs.listStatus(locationPath) ;
      if(status == null || status.length == 0) return ;
      for(int i = 0; i < status.length; i++) {
        Path path = status[i].getPath() ;
        holder.add(new Segment(dbconfiguration.getHadoopConfiguration(), location, path.getName())) ;
      }
      Collections.sort(holder) ;
    }
    this.segments = holder.toArray(new Segment[holder.size()]) ;
  }

  synchronized public Segment[] getSegments() { return segments ; }

  synchronized public Segment newSegment() throws IOException {
    Segment[] temp = new Segment[segments.length + 1] ;
    for(int i = 0; i < segments.length; i++) {
      temp[i] = segments[i] ;
    }
    String segmentName = null ;
    if(segments.length == 0) {
      segmentName = Segment.createSegmentName(0) ;
    } else {
      segmentName = Segment.createSegmentName(segments[segments.length - 1].getIndex() + 1) ;
    }
    temp[segments.length] = 
        new Segment(dbconfiguration.getHadoopConfiguration(), location, segmentName) ;
    this.segments = temp ;
    return this.segments[segments.length - 1] ;
  }

  synchronized public MultiSegmentIterator getMultiSegmentIterator() throws IOException {
    MultiSegmentIterator iterator = new MultiSegmentIterator(this.segments) ;
    return iterator ;
  }

  public ColumnFamilyIterator getColumnFamilyIterator() throws IOException {
    return new ColumnFamilyIterator(getMultiSegmentIterator(), createCellMerger()) ;
  }

  synchronized public void autoCompact(Reporter reporter) throws IOException {
    if(this.segments.length == 1) return ;
    Segment psegment = null ;
    List<Segment> mergeSegments = new ArrayList<Segment>() ;
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
    Segment[] msegments = mergeSegments.toArray(new Segment[mergeSegments.size()]) ;
    MultiSegmentIterator iterator = new MultiSegmentIterator(msegments) ;
    ColumnFamilyIterator mitr = new ColumnFamilyIterator(iterator, createCellMerger()) ;
    Segment.Writer writer = msegments[0].getWriter() ;
    String counterName = "Compact Entity " + columnDef.getName() ;
    while(mitr.next()) {
      RowId key = mitr.currentKey() ;
      Cell value = mitr.currentValue() ;
      writer.append(key, value) ;
      if(reporter != null) reporter.increment(counterName, 1) ;
    }
    iterator.close() ;
    mitr.close() ;

    writer.close() ;
    for(int i = 1; i < msegments.length; i++) {
      msegments[i].delete() ;
    }
    reload() ;
  }

  protected CellMerger createCellMerger() { return new CellMerger.LatestCellMerger() ; }
}