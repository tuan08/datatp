package net.datatp.nlp.wtag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagDocumentWriter {
  public void write(File file, TokenCollection[] collection) throws IOException {
    PrintStream out = new PrintStream(new FileOutputStream(file), true, "UTF-8");
    write(out, collection) ;
    out.close() ;
  }

  public void write(PrintStream out, TokenCollection[] collection) throws IOException {
    for(int i = 0; i < collection.length; i++) {
      IToken[] token = collection[i].getTokens() ;
      for(int j = 0; j < token.length; j++) {
        if(j > 0) out.print(' ') ;
        write(out, token[j]) ;
      }
    }
  }

  protected void write(PrintStream out, IToken token) throws IOException {
    out.append(token.getOriginalForm());
    out.append(":{") ;
    WTagBoundaryTag btag = token.getFirstTagType(WTagBoundaryTag.class) ;
    if(btag != null) {
      String[] feature = btag.getFeatures() ;
      for(int i = 0; i < feature.length; i++) {
        if(i > 0) out.append(", ") ;
        out.append(feature[i]) ;
      }
    }
    out.append("}") ;
  }
}