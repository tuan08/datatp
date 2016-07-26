package net.datatp.util.json;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSONWriter {
  private OutputStream out ;
  private JsonGenerator generator ;

  public JSONWriter(String file) throws Exception {
    this(file, file.endsWith(".gzip")) ;
  }

  public JSONWriter(String file, boolean compress) throws Exception {
    if(compress) {
      init(new GZIPOutputStream(new FileOutputStream(file))) ;
    } else {
      init(new FileOutputStream(file)) ;
    }
  }

  public JSONWriter(OutputStream os) throws Exception {
    init(os) ;
  }

  public JSONWriter(PrintStream out) throws IOException {
    init(out) ;
  }

  private void init(OutputStream out) throws IOException {
    this.out = out ;
    MappingJsonFactory factory = new MappingJsonFactory();
    JSONSerializer.configure(factory.getCodec());
    generator = factory.createGenerator(out, JsonEncoding.UTF8) ;
  }

  synchronized public void write(Object object) throws Exception {
    generator.writeObject(object) ;
  }

  public void close() throws IOException {
    generator.close() ;
    out.flush() ;
    out.close() ;
  }
}