package net.datatp.nlp.token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenException extends Exception {
  private static final long serialVersionUID = 1L;

  public TokenException(String message) {
  	super(message) ;
  }
  
  public TokenException(String message, Throwable rootCause) {
  	super(message, rootCause) ;
  }
}
