package net.datatp.storage.batchdb.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 15, 2010  
 */
abstract public class MapHolder<T extends Writable> extends HashMap<String, T> implements Writable {
  private static final long serialVersionUID = 1L;

  public void readFields(DataInput in) throws IOException {
    int size = in.readInt() ;
    for(int i = 0; i < size; i++) {
      String key = WritableUtils.readString(in) ;
      T instance = createInstance() ;
      instance.readFields(in) ;
      put(key, instance) ;
    }
  }

  public void write(DataOutput out) throws IOException {
    out.writeInt(size()) ;
    Iterator<Map.Entry<String, T>> i = entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, T> entry = i.next() ;
      WritableUtils.writeString(out, entry.getKey()) ;
      entry.getValue().write(out) ;
    }
  }

  abstract public T createInstance() ;
}
