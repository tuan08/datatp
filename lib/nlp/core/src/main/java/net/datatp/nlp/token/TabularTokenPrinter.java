package net.datatp.nlp.token;

import java.io.PrintStream;
import java.util.List;

import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.nlp.util.CharacterSet;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TabularTokenPrinter extends TokenPrinter {
  private String[] printTag;

  public TabularTokenPrinter() { }

  public TabularTokenPrinter(String[] printTag) { 
    this.printTag = printTag ;
  }

  public void printCollection(PrintStream out, TokenCollection[] collection) {
    for(int i = 0; i < collection.length; i++) {
      print(out, collection[i]) ;
    }
  }

  public void print(PrintStream out, TokenCollection[] collection) {
    for(int i = 0; i < collection.length; i++) {
      print(out, collection[i].getTokens()) ;
      out.println();
    }
  }

  public void print(PrintStream out, IToken[] token) {
    for(int i = 0; i < token.length; i++) {
      print(out, token[i]) ;
    }
  }

  private void print(PrintStream out, IToken token) {
    String label = token.getOriginalForm() ;
    if(label.length() == 1 && CharacterSet.isIn(label.charAt(0), CharacterSet.NEW_LINE)) {
      label = "NEW LINE" ;
    }
    printCol(out, label, 25) ;
    List<TokenTag> tags = token.getTag() ;
    if(tags != null) {
      for(TokenTag sel : tags) {
        if(isPrintTag(sel)) {
          printCol(out, sel) ;
          out.print(' ');
        }
      }
    }
    out.println() ;
  }

  private void printCol(PrintStream out, String label, int width) {
    out.print(label) ;
    for(int i = label.length(); i < width; i++) {
      out.print(' ') ;
    }
  }

  protected void printCol(PrintStream out, TokenTag sel) {
    out.append(sel.getInfo()) ;
  }

  protected boolean isPrintTag(TokenTag sel) {
    if(printTag == null) return true ;
    return StringUtil.isIn(sel.getOType(), printTag) ;
  }
}
