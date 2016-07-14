package net.datatp.storage.kvdb;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 20, 2010  
 */
public interface RecordMerger<R extends Record> {
  public R merge(R r1, R r2) ;

  static public class LatestRecordMerger<R extends Record> implements RecordMerger<R> {
    public R merge(R r1, R r2) { return r2; }
  }
}
