package net.datatp.nlp.token;


public class SingleSequenceIterator implements TokenSequenceIterator {
	private IToken[] tokens ;
	private boolean  read = false ;
	
	public SingleSequenceIterator(IToken[] tokens) {
		this.tokens = tokens ;
	}
	
  public TokenCollection next() throws TokenException {
  	if(read) return null ;
  	read = true ;
  	return new TokenCollection(tokens, 0, tokens.length);
  }
}
