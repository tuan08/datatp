package net.datatp.nlp.token;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.tag.TokenTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenVerifier {
  private String expectOrigForm ;
  private List<TagVerifier> expectTag = new ArrayList<TagVerifier>(3);

  public TokenVerifier(String exp) {
    if(exp.indexOf('{') > 0 && exp.indexOf('}') > 0) {
      int idx  = exp.indexOf('{') ;
      expectOrigForm = exp.substring(0, idx) ;
      String params = exp.substring(idx + 1, exp.length() - 1) ;
      String[] nameValue = params.split(",");
      for(int i = 0; i < nameValue.length; i++) {
        nameValue[i] = nameValue[i].trim() ;
        String name = nameValue[i], value = null ;
        int equalIdx = nameValue[i].indexOf('=') ;
        if(equalIdx > 0) {
          name = nameValue[i].substring(0, equalIdx).trim() ;
          value = nameValue[i].substring(equalIdx + 1).trim() ;
        }
        expectTag.add(new TagVerifier(name, value)) ;
      }
    } else {
      expectOrigForm = exp ;
    }
  }

  public void verify(IToken token) {
    if(!expectOrigForm.equals(token.getOriginalForm())) {
      throw new RuntimeException("Expect Token " + expectOrigForm + ", but " + token.getOriginalForm()) ;
    }

    List<TokenTag> tokenTag = token.getTag() ;
    for(int i = 0; i < expectTag.size(); i++) {
      TagVerifier tverifier = expectTag.get(i) ;
      boolean found = false ;
      for(int j = 0; j < tokenTag.size(); j++) {
        TokenTag tag = tokenTag.get(j) ;
        if(tverifier.matches(tag)) {
          found = true;
          break ;
        }
      }
      if(!found) {
        throw new RuntimeException("Expect tag " + tverifier + " token " + token.getOriginalForm()) ;
      }
    }
  }

  static class TagVerifier {
    private String otype ;
    private String expectValue ;

    public TagVerifier(String otype, String expectValue) {
      this.otype = otype ;
      this.expectValue = expectValue ;
    }

    public boolean matches(TokenTag tag) {
      if(!otype.equals(tag.getOType())) return false;
      if(expectValue == null) return true ;
      return expectValue.equals(tag.getTagValue()) ;
    }

    public String toString() {
      if(expectValue == null) return otype ;
      return otype + " = " + expectValue ;
    }
  }

}