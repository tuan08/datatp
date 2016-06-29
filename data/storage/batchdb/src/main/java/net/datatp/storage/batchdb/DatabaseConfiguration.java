package net.datatp.storage.batchdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class DatabaseConfiguration implements Writable {
  final static public String CONF_FILE = "database.conf" ;

  private RowIdPartitioner   rowIdPartitioner;
  private ColumnDefinition[] columnDefinitions;
  private String             location;
  private Configuration      hdconfiguration;

  public DatabaseConfiguration() {

  }

  public DatabaseConfiguration(ColumnDefinition[] columnDefinitions, RowIdPartitioner rowIdPartitioner) {
    this.columnDefinitions = columnDefinitions ;
    this.rowIdPartitioner = rowIdPartitioner ;
  }

  public RowIdPartitioner getRowIdPartitioner() { 
    return this.rowIdPartitioner ; 
  }

  public String getLocation() { return this.location ; }
  public void   setLocation(String loc) { this.location = loc ;}

  public ColumnDefinition[] getColumnDefinition() { return this.columnDefinitions ; }

  public Configuration getHadoopConfiguration() { 
    return this.hdconfiguration ; 
  }

  public void setHadoopConfiguration(Configuration conf) {
    this.hdconfiguration = conf ;
  }

  public ColumnDefinition getColumnDefinition(String name) {
    for(ColumnDefinition def : columnDefinitions) {
      if(def.getName().equals(name)) return def ;
    }
    return null ;
  }

  public void readFields(DataInput in) throws IOException {
    try {
      Class clazz = Class.forName(Text.readString(in)) ;
      this.rowIdPartitioner = (RowIdPartitioner) clazz.newInstance() ;
    } catch(Exception ex) {
      throw new IOException(ex) ;
    }
    rowIdPartitioner.readFields(in) ;

    this.columnDefinitions = new ColumnDefinition[in.readInt()] ;
    for(int i = 0; i < columnDefinitions.length; i++) {
      columnDefinitions[i] = new ColumnDefinition() ;
      columnDefinitions[i].readFields(in) ;
    }
  }

  public void write(DataOutput out) throws IOException {
    Text.writeString(out, rowIdPartitioner.getClass().getName()) ;
    rowIdPartitioner.write(out) ;

    out.writeInt(this.columnDefinitions.length) ;
    for(int i = 0; i < columnDefinitions.length; i++) {
      columnDefinitions[i].write(out) ;
    }
  }
}