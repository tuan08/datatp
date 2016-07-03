package net.datatp.storage.kvdb;

import org.apache.hadoop.io.Writable;

public interface RecordUpdater<T extends Record> {
	public T update(Writable key, T record) ;
}
