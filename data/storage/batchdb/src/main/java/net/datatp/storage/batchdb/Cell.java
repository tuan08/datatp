package net.datatp.storage.batchdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


public class Cell extends TreeMap<byte[], byte[]> implements Writable {
  private static final long serialVersionUID = 1L;

  final static public Cell NULL_CELL = new Cell() ;

  public static Comparator<byte[]> KEY_COMPARATOR = new Comparator<byte[]>() {
    public int compare(byte[] o1, byte[] o2) {
      return Bytes.compareTo(o1, 0, o1.length, o2, 0, o2.length);
    }
  };

  private String name ;
  private RowId key ;

  public Cell() { super(KEY_COMPARATOR) ; } 

  public String getName() { return this.name ; }
  public void   setName(String name) { this.name = name; }

  public RowId  getKey() { return this.key ; }
  public void setKey(RowId key) { this.key = key ; }

  public String getFieldAsString(String name) {
    byte[] value = get(Bytes.toBytes((name))) ;
    if(value == null) {
      removeField(name) ;
      return null ;
    } else return Bytes.toString(value) ;
  }

  public Text getFieldAsText(String name) { return getFieldAsText(new Text(name)) ; }

  public Text getFieldAsText(Text name) {
    byte[] value = get(TextUtil.getBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return null ;
    } else return new Text(value) ;
  }

  public byte getFieldAsByte(String name) {
    return get(Bytes.toBytes(name))[0] ;
  }

  public boolean getFieldAsBoolean(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return false ;
    } else return Bytes.toBoolean(value) ;
  }

  public short getFieldAsShort(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return 0 ;
    } else return Bytes.toShort(value) ;
  }

  public int getFieldAsInt(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return 0 ;
    } else return Bytes.toInt(value) ;
  }

  public float getFieldAsFloat(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return 0 ;
    } else return Bytes.toFloat(value) ;
  }

  public double getFieldAsDouble(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return 0 ;
    } else return Bytes.toDouble(value) ;
  }

  public long getFieldAsLong(String name) {
    byte[] value = get(Bytes.toBytes(name)) ;
    if(value == null) {
      removeField(name) ;
      return 0 ;
    } else return Bytes.toLong(value) ;
  }

  public String[] getFieldAsStringArray(String name) throws IOException{
    byte[] value = get(Bytes.toBytes(name));
    if(value == null){
      removeField(name);
      return null;
    } else return TextUtil.fromBytes(value);
  }

  public byte[] getFieldAsBytes(String name) {
    return get(Bytes.toBytes(name)) ;
  }

  public void addField(Text name, byte[] bytes) {
    put(TextUtil.getBytes(name), bytes) ;
  }

  public void addField(String name, byte[] bytes) {
    put(Bytes.toBytes(name), bytes) ;
  }

  public void addField(String name, boolean value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, byte value) {
    put(Bytes.toBytes(name), new byte[] { value }) ;
  }

  public void addField(String name, short value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, int value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, float value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, double value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, long value) {
    put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, String value) {
    if(value == null) removeField(name) ;
    else put(Bytes.toBytes(name), Bytes.toBytes(value)) ;
  }

  public void addField(String name, String[] value) throws IOException {
    if(value == null) removeField(name) ;
    else put(Bytes.toBytes(name), TextUtil.toBytes(value)) ;
  }

  public void addField(String name, Text value) {
    if(value == null) removeField(name) ;
    else put(Bytes.toBytes(name), TextUtil.getBytes(value)) ;
  }

  public void addField(Text name, Text value) {
    if(value == null) removeField(name) ;
    else put(TextUtil.getBytes(name), TextUtil.getBytes(value)) ;
  }

  public void addField(byte[] key, byte[] value) {
    if(value == null) remove(key) ;
    else put(key, value) ;
  }

  public void removeField(String name) { remove(Bytes.toBytes(name)) ; }

  public void removeField(Text name) {
    remove(TextUtil.getBytes(name)) ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeInt(size()) ;
    Iterator<Map.Entry<byte[], byte[]>> i = this.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<byte[], byte[]> entry = i.next() ;
      int length = -1 ;
      byte[] key = entry.getKey() ;
      length = key.length ;
      out.writeInt(length) ;
      out.write(key) ;

      byte[] value = entry.getValue() ;
      length = value.length ;
      out.writeInt(length) ;
      out.write(value) ;
    }
  }

  public void readFields(DataInput in) throws IOException {
    int size = in.readInt() ;
    for(int i = 0; i < size; i++) {
      byte[] key = new byte[in.readInt()] ;
      in.readFully(key) ;
      byte[] value = new byte[in.readInt()] ;
      in.readFully(value) ;
      put(key, value) ;
    }
  }
}