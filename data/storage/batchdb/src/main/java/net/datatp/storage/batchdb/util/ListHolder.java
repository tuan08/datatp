package net.datatp.storage.batchdb.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList ;
import org.apache.hadoop.io.Writable;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 15, 2010  
 */
abstract public class ListHolder<T extends Writable> extends ArrayList<T > implements Writable {
  private static final long serialVersionUID = 1L;

  public void readFields(DataInput in) throws IOException {
    int size = in.readInt() ;
    for(int i = 0; i < size; i++) {
      T instance = createInstance();
      instance.readFields(in) ;
      add(instance) ;
    }
  }

  public void write(DataOutput out) throws IOException {
    out.writeInt(size()) ;
    for(int i = 0; i < size(); i++) {
      T instance = get(i) ;
      instance.write(out) ;
    }
  }

  abstract public T createInstance() ;
}
