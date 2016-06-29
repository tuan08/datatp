package net.datatp.storage.batchdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class ColumnDefinition implements Writable {
	private String name ;
	
	public ColumnDefinition() {
		
	}
	
  public ColumnDefinition(String name) {
		this.name = name ;
	}
	
	public String getName() { return this.name ; }
	public void   setName(String name) { this.name = name ; }

  public void readFields(DataInput in) throws IOException {
  	this.name = Text.readString(in) ;
  }

  public void write(DataOutput out) throws IOException {
  	Text.writeString(out, name) ;
  }
}
