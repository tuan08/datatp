package net.datatp.storage.batchdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class RowId implements WritableComparable<RowId> {
	final static public byte STORE_STATE = 0 ;
	final static public byte DELETE_STATE = 1 ;
	
	private byte[] key ;
	private long   createdTime ;
	private long   modifiedTime ;
	private byte   state ;

	public RowId() {}
	
	public RowId(RowId other) {
	  init(other.key, other.createdTime, other.modifiedTime, other.state) ;
	}
	
	public RowId(int key, long createTime, long modifiedTime, byte state) {
	  init(Bytes.toBytes(key), createTime, modifiedTime, state) ;
	}
	
	public RowId(long key, long createTime, long modifiedTime, byte state) {
    init(Bytes.toBytes(key), createTime, modifiedTime, state) ;
  }
	
	public RowId(String key, long createTime, long modifiedTime, byte state) {
	  init(Bytes.toBytes(key), createTime, modifiedTime, state) ;
	}
	
	public RowId(Text key, long createdTime, long modifiedTime, byte state) {
	  init(TextUtil.getBytes(key), createdTime, modifiedTime, state) ;
	}

	public RowId(byte[] key, long createdTime, long modifiedTime, byte state) {
	  init(key, createdTime, modifiedTime, state) ;
	}
	
	private void init(byte[] key, long createdTime, long modifiedTime, byte state) {
	  this.key = key ;
	  if(createdTime > 0) this.createdTime = createdTime ;
	  else this.createdTime = System.currentTimeMillis() ;
	  if(modifiedTime > 0) this.modifiedTime = modifiedTime ;
	  else this.modifiedTime = this.createdTime ;
	  this.state = state ;
	}

	public byte[] getKey() { return key; }
	public void setKey(byte[] key) { this.key = key; }
	public void setKey(Text key) { this.key = TextUtil.getBytes(key); }

	public long getCreatedTime() { return createdTime; }
	public void setCreatedTime(long time) { this.createdTime = time; }

	public long getModifiedTime() { return modifiedTime ; }
	public void setModifiedTime(long time) { this.modifiedTime = time ; }
	
	public byte getState() { return state; }
	public void setState(byte state) { this.state = state; }

  public void readFields(DataInput in) throws IOException {
    this.key = new byte[in.readInt()] ;
    in.readFully(key) ;
  	this.createdTime  = in.readLong() ;
  	this.modifiedTime = in.readLong() ;
  	this.state = in.readByte() ;
  }

  public void write(DataOutput out) throws IOException {
    out.writeInt(key.length) ;
    out.write(key) ;
  	out.writeLong(createdTime) ;
  	out.writeLong(modifiedTime) ;
  	out.writeByte(state) ;
  }

  public int compareTo(RowId other) { 
  	return Bytes.compareTo(key, 0, key.length, other.getKey(), 0, other.getKey().length); 
  }
}
