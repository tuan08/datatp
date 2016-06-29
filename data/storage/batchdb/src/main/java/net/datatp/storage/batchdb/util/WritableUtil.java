package net.datatp.storage.batchdb.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jun 4, 2010  
 */
public class WritableUtil {
  static public byte[] getBytes(Text text) {
    if(text == null) return null ;
    byte[] data = new byte[text.getLength()] ;
    System.arraycopy(text.getBytes(), 0, data, 0, data.length) ;
    return data ;
  }
  
  static public void writeText(DataOutput out, Text text) throws IOException {
    if(text != null) {
      out.writeBoolean(true) ;
      text.write(out) ;
    } else {
      out.writeBoolean(false) ;
    }
  }
  
  static public Text readText(DataInput in) throws IOException {
    boolean b = in.readBoolean() ;
    if(b) {
      Text text = new Text() ;
      text.readFields(in) ;
      return text ;
    }
    return null ;
  }
  
  static public void writeBytes(DataOutput out, byte[] value) throws IOException {
    if(value != null) {
      out.writeInt(value.length) ;
      out.write(value) ;
    } else {
      out.writeInt(-1) ;
    }
  }
  
  static public byte[] readBytes(DataInput in) throws IOException {
    int size = in.readInt() ;
    if(size >= 0) {
      byte[] bytes = new byte[size] ;
      in.readFully(bytes) ;
      return bytes ;
    }
    return null ;
  }
  
  final static public Text text(String string) {
    if(string == null) return null ;
    return new Text(string) ;
  }
}
