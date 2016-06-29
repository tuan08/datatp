package net.datatp.storage.batchdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Database {
	private String location ;
	private DatabaseConfiguration dbconfiguration ;
	private RowIdPartitioner rowIdPartitioner ;
	private RowDB[] partitionRowDbs ;
	
	private Database(String location, DatabaseConfiguration dbconf) throws IOException {
		this.location = location ;
		this.dbconfiguration = dbconf ;
		this.rowIdPartitioner = dbconf.getRowIdPartitioner() ;
		this.partitionRowDbs = new RowDB[rowIdPartitioner.getNumberOfPartition()] ;
		for(int i = 0; i < partitionRowDbs.length; i++) {
			String name = "partition-" + i ;
			partitionRowDbs[i] = new RowDB(location, name, dbconf) ;
			partitionRowDbs[i].setPartitionId(i) ;
		}
	}
	
	public String getLocation() { return this.location ; }
	
	public DatabaseConfiguration getDatabaseConfiguration() { return this.dbconfiguration ; }
	
	public RowIdPartitioner getRowIdPartitioner() { return this.rowIdPartitioner ; }
	
	public RowDB getRowDB(int partition) { return partitionRowDbs[partition] ; }
	
	public RowDB[] getRowDB() { return partitionRowDbs ; }
	
	public void autoCompact(Reporter reporter) throws IOException {
		for(RowDB sel : getRowDB()) sel.autoCompact(reporter) ;
	}
	
	public Reader getReader() throws IOException { return new Reader() ; }
	
	public Reader getReader(String[] column) throws IOException { 
		return new Reader() ; 
	}
	
	public Writer getWriter() { return new Writer() ; }
	
	public void removeColumnDB(String name) throws IOException {
		for(RowDB sel : partitionRowDbs) {
			sel.removeColumnDB(name) ;
		}
	}
	
	public class Reader {
		private Row[] row ;
		private RowDB.RowReader[] reader  ;
		
		public Reader() throws IOException {
			row = new Row[partitionRowDbs.length] ;
			reader = new RowDB.RowReader[partitionRowDbs.length] ;
			for(int i = 0; i < reader.length; i++) {
				reader[i] = partitionRowDbs[i].getRowReader() ;
			}
		}
		
		public Reader(String[] column) throws IOException {
			row = new Row[partitionRowDbs.length] ;
			reader = new RowDB.RowReader[partitionRowDbs.length] ;
			for(int i = 0; i < reader.length; i++) {
				reader[i] = partitionRowDbs[i].getRowReader(column) ;
			}
		}
		
		public Row next() throws IOException {
			for(int i = 0; i < reader.length; i++) {
				if(row[i] == null) row[i] = reader[i].next() ;
			}
			int selectRow = -1 ;
			for(int i = 0; i < row.length; i++) {
				if(row[i] == null) continue ;
				if(selectRow < 0) {
					selectRow = i ;
				} else {
					int compare = row[i].getRowId().compareTo(row[selectRow].getRowId()) ; 
					if(compare < 0) selectRow = i ;
				}
			}
			if(selectRow >= 0) {
				Row select = row[selectRow] ;
				row[selectRow] = null ;
				return select ;
			}
			return null ;
		}
		
		public void close() throws IOException {
			for(int i = 0; i < reader.length; i++) reader[i].close() ;
		}
	}
	
	public class Writer {
		public void write(RowId rowId, Row row, Reporter reporter) throws IOException {
			int partition = rowIdPartitioner.getPartition(rowId) ;
			partitionRowDbs[partition].getCurrentWriter().write(rowId, row, reporter) ;
		}
		
		public void write(Row row, Reporter reporter) throws IOException {
			RowId rowId = row.getRowId() ;
			int partition = rowIdPartitioner.getPartition(rowId) ;
			partitionRowDbs[partition].getCurrentWriter().write(rowId, row, reporter) ;
		}
		
		public void close() throws IOException {
			for(int i = 0; i < partitionRowDbs.length; i++) {
				partitionRowDbs[i].closeCurrentWriter() ;
			}
		}
	}
	
	static public Database getDatabase(String location, Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf) ;
		Path confPath = new Path(location + "/" + DatabaseConfiguration.CONF_FILE) ;
		if(fs.exists(confPath)) {
			DatabaseConfiguration dbconf = new DatabaseConfiguration() ;
			InputStream is = fs.open(confPath) ;
			ObjectInputStream in = new ObjectInputStream(is) ;
			dbconf.readFields(in) ;
			in.close() ;
			dbconf.setHadoopConfiguration(conf) ;
			Database database = new Database(location, dbconf) ;
			return database ;
		} 
		return null ;
	}
	
	static public Database getDatabase(String location, DatabaseConfiguration dbconf) throws IOException {
		Configuration conf = dbconf.getHadoopConfiguration() ;
		FileSystem fs = FileSystem.get(dbconf.getHadoopConfiguration()) ;
		Path confPath = new Path(location + "/" + DatabaseConfiguration.CONF_FILE) ;
		if(fs.exists(confPath)) {
			dbconf = new DatabaseConfiguration() ;
			InputStream is = fs.open(confPath) ;
	    ObjectInputStream in = new ObjectInputStream(is) ;
	    dbconf.readFields(in) ;
	    in.close() ;
	    dbconf.setHadoopConfiguration(conf) ;
		} else {
			OutputStream os = fs.create(confPath) ;
			ObjectOutputStream out = new ObjectOutputStream(os) ;
			dbconf.write(out) ;
			out.close() ;
		}
		Database database = new Database(location, dbconf) ;
		return database ; 
	}
	
	static public DatabaseConfiguration getDatabaseConfiguration(String loc, Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf) ;
		Path confPath = new Path(loc + "/" + DatabaseConfiguration.CONF_FILE) ;
		if(fs.exists(confPath)) {
			DatabaseConfiguration dbconf = new DatabaseConfiguration() ;
			InputStream is = fs.open(confPath) ;
			ObjectInputStream in = new ObjectInputStream(is) ;
			dbconf.readFields(in) ;
			in.close() ;
			dbconf.setHadoopConfiguration(conf) ;
			return dbconf ;
		}
		return null ;
	}
}