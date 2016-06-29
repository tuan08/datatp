package net.datatp.storage.batchdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public interface RowIdPartitioner extends Writable {
	public int getNumberOfPartition() ;
	
	public int getPartition(RowId rowId) ;
	
	static public class RowIdHashPartioner implements RowIdPartitioner {
		private int numberOfPartition = 1;
		private String prefixDelimiter ;
		
		public RowIdHashPartioner() {}
		
		public RowIdHashPartioner (int num, String prefixDelimiter) {
			this.numberOfPartition = num ;
			this.prefixDelimiter = prefixDelimiter ;
		}
		
    public int getNumberOfPartition() { return numberOfPartition ; }
    
    public int getPartition(RowId rowId) {
    	String id = Bytes.toString(rowId.getKey()) ;
    	if(prefixDelimiter != null) {
    		int idx = id.indexOf(prefixDelimiter) ;
    		if(idx > 0) id = id.substring(0, idx) ;
    	}
    	int hashCode = id.hashCode() ;
    	return  Math.abs(hashCode) % numberOfPartition ;
    }

    public void readFields(DataInput in) throws IOException {
    	this.numberOfPartition = in.readInt() ;
    	this.prefixDelimiter = Text.readString(in) ;
    	if(this.prefixDelimiter.length() == 0) this.prefixDelimiter = null ;
    }

    public void write(DataOutput out) throws IOException {
    	out.writeInt(numberOfPartition) ;
    	if(prefixDelimiter == null) Text.writeString(out, "") ;
    	else Text.writeString(out, prefixDelimiter) ;
    }
	}
}
