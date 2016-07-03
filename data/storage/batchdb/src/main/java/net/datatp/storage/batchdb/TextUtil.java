package net.datatp.storage.batchdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Text;

import net.datatp.util.text.StringUtil;

public class TextUtil {

  public static byte[] getBytes(Text text) {
    byte[] value = null ;
    if(text.getBytes().length > text.getLength()) {
      value = new byte[text.getLength()] ;
      System.arraycopy(text.getBytes(), 0, value, 0, value.length) ;
    } else value = text.getBytes() ;
    return value ;
  }


  static public byte[] toBytes(String[] string) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    DataOutput out = new DataOutputStream(bos) ;
    if(string == null) {
      out.writeInt(-1) ;
    } else {
      out.writeInt(string.length) ;
      for(int i = 0; i < string.length; i++) {
        if(string[i] == null) {
          out.writeInt(-1) ;
        } else {
          byte[] buffer = string[i].getBytes(StringUtil.UTF8);
          out.writeInt(buffer.length) ;
          out.write(buffer) ;
        }
      }
    }
    return bos.toByteArray() ;
  }

  static public String[] fromBytes(byte[] bytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes) ;
    DataInput in = new DataInputStream(bis) ;
    int len = in.readInt() ;
    if(len < 0) return null ;
    String[] string = new String[len] ;
    for(int i = 0; i < string.length; i++) {
      int size = in.readInt() ;
      if(size < 0) {
        string[i] = null ;
      } else {
        byte[] buffer = new byte[size] ;
        in.readFully(buffer) ;
        string[i] = new String(buffer, StringUtil.UTF8) ;
      }
    }
    return string ;
  }
}
