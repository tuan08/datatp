package net.datatp.webcrawler.urldb;

import java.io.IOException;

import org.apache.hadoop.io.Text;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDBIterator;
import net.datatp.storage.kvdb.MergeMultiSegmentIterator;

public class URLDatumRecordDBIterator implements URLDatumDBIterator {
  private MergeMultiSegmentIterator<Text, URLDatumRecord> mitr;
  private URLDatum currentURLDatum;
  
  public URLDatumRecordDBIterator(URLDatumRecordDB urlDatumDB) throws Exception {
    mitr = urlDatumDB.getMergeRecordIterator() ;
  }
  
  public boolean hasNext() throws IOException {
    currentURLDatum = null;
    if(mitr.next()) {
      currentURLDatum = mitr.currentValue() ;
      return true;
    }
    return false;
  }
  
  public URLDatum next() {
    URLDatum ret = currentURLDatum;
    currentURLDatum = null;
    return ret;
  }
  
  public void close() throws IOException {
    mitr.close();
  }
}
