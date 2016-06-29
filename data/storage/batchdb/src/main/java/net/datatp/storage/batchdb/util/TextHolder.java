package net.datatp.storage.batchdb.util;

import org.apache.hadoop.io.Text;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 19, 2010  
 */
public class TextHolder extends ListHolder<Text> {
  private static final long serialVersionUID = 1L;

  public Text createInstance() { return new Text(); }

  public String[] toStringArray() {
    String[] array = new String[size()] ;
    for(int i = 0; i < array.length; i++) array[i] = get(i).toString() ;
    return array ;
  }
}
