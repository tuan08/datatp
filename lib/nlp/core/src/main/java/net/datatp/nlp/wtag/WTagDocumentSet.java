package net.datatp.nlp.wtag;

import java.io.IOException;

import net.datatp.util.io.FileUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagDocumentSet {
  private String[] files ;

  public WTagDocumentSet(String dir, String pattern) throws IOException {
    this.files = FileUtil.findFiles(dir, pattern) ;
  }

  public int size() { return files.length; }

  public String getFile(int i) { return files[i] ; }

  public String[] getFiles() { return files ; }
}