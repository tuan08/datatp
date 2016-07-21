package net.datatp.webcrawler.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import net.datatp.util.text.StringUtil;

public class EncodingDetector {
  static public EncodingDetector INSTANCE = new EncodingDetector();
  
  public EncodingDetector() {
  }

  public Charset detect(InputStream is, int length) throws IllegalArgumentException, IOException {
    return StringUtil.UTF8 ;
  }
  
  public Charset detect(byte[] data, int length) throws IllegalArgumentException, IOException {
    return StringUtil.UTF8 ;
  }
  
  static public EncodingDetector getInstance() { return INSTANCE; }
}
