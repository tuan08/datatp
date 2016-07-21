package net.datatp.webcrawler.urldb;

import java.io.IOException;

import org.apache.hadoop.io.Text;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDBWriter;
import net.datatp.storage.hdfs.SortKeyValueFile;

public class URLDatumRecordDBWriter  implements URLDatumDBWriter {
  private URLDatumRecordDB urlDatumDB;
  private SortKeyValueFile<Text, URLDatumRecord>.Writer writer;
  
  public URLDatumRecordDBWriter(URLDatumRecordDB urlDatumDB) throws Exception {
    this.urlDatumDB = urlDatumDB;
    writer = urlDatumDB.newSegment().getWriter();
  }
  
  public URLDatum createURLDatumInstance(long timestamp) {
    return new URLDatumRecord(timestamp);
  }
  
  public void write(URLDatum urlDatum) throws IOException {
    URLDatumRecord record = (URLDatumRecord) urlDatum;
    writer.append(new Text(record.getId()), record) ;
  }
  
  public void optimize() throws Exception {
    urlDatumDB.autoCompact();
  }
  
  public void close() throws IOException {
    writer.close();
  }
}
