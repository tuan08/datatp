package net.datatp.util;

import java.io.IOException;
import java.io.PrintStream;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ConsoleUtil {
  static public PrintStream getUTF8SuportOutput() {
    try {
      PrintStream out = new PrintStream(System.out, true, "UTF-8") ;
      return out ;
    } catch(IOException ex) {

    }
    return null ;
  }
}
