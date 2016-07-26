package net.datatp.nlp.vi ;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.datatp.util.ConsoleUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class VnSyllable {
  final static public String VN_MAIN_VOWELS = "a|â|ă|e|ê|i|ia|iê|o|oo|ơ|ô|u|ươ|ưa|uô|ua|ư|y|ya|yê";
  private static String vnVowels = "aáàảãạăắằẳẵặâấầẩẫậeéèẻẽẹêếềểễệiíìỉĩịoóòỏõọôốồổỗộơớờởỡợuúùủũụưứừửữựyýỳỷỹỵ" ;

  public static final String EMPTY = "";

  private static ArrayList alMainVowels;

  static {
    alMainVowels = new ArrayList();
    initArrayList(alMainVowels, VN_MAIN_VOWELS);
  }


  private char[] syllableBuf;

  private String strMainVowel = "";
  private String strSecondaryVowel = "";
  private String strFirstConsonant =  "";
  private String strLastConsonant = "" ;

  private TONE tone = TONE.NO_TONE;

  private int iCurPos = 0 ;
  private boolean validViSyll = true ; 

  public VnSyllable(String syll) {
    syllableBuf = syll.toCharArray() ;
    parseFirstConsonant();

    vnSecondaryVowel();
    parseMainVowel();
    parseLastConsonant();
  }

  public String getFirstConsonant() { return strFirstConsonant; }
  public String getSecondVowel() { return strSecondaryVowel; }
  public String getMainVowel() { return strMainVowel; }
  public String getLastConsonant() { return strLastConsonant; }

  public TONE getTone() { return tone; }

  public String getRhyme(){ return strSecondaryVowel + strMainVowel + strLastConsonant; }

  public String getNonToneSyll(){
    return strFirstConsonant + strSecondaryVowel + strMainVowel + strLastConsonant;
  }

  public boolean isValidVnSyllable() { return validViSyll ; }

  private void parseFirstConsonant() {
    String fconsonant = vnFirstConsonants(syllableBuf, 0) ;
    if(fconsonant != null) {
      strFirstConsonant = fconsonant ;
      iCurPos += fconsonant.length() ;
    } else {
      strFirstConsonant = EMPTY;
    }
  }

  private void vnSecondaryVowel() {
    if (!validViSyll) return;
    if (iCurPos > syllableBuf.length - 1) {
      validViSyll = false;
      return;
    }
    // get the current and next character in the syllable string
    char curChar, nextChar = '$';
    curChar = syllableBuf[iCurPos];
    if (iCurPos + 1 < syllableBuf.length)
      nextChar = syllableBuf[iCurPos + 1];

    // get the tone and the original vowel (without tone)
    TONE tone = TONE.NO_TONE;
    int idx1 = vnVowels.indexOf(curChar);
    int idx2 = vnVowels.indexOf(nextChar);

    if (idx1 == -1) return;// current char is not a vowel
    tone = TONE.getTone(idx1 % 6);
    curChar = vnVowels.charAt((idx1 / 6) * 6);

    if (idx2 == -1) { // next char is not a vowel
      strSecondaryVowel = EMPTY;
      return;
    }
    nextChar = vnVowels.charAt((idx2 / 6) * 6);
    if (tone.getValue() == TONE.NO_TONE.getValue()) tone = TONE.getTone(idx2 % 6);

    // Check the secondary vowel
    if (curChar == 'o') {
      if (nextChar == 'a' || nextChar == 'e') {
        strSecondaryVowel += curChar;
        iCurPos++;
      } else
        strSecondaryVowel = EMPTY; // oo
      return;
    } else if (curChar == 'u') {
      if (nextChar != 'i' && nextChar != '$') {
        strSecondaryVowel += curChar;
        iCurPos++;
      } else
        strSecondaryVowel = EMPTY;
      return;
    }
  }

  private void parseMainVowel() {
    if (!validViSyll) return;
    if (iCurPos > syllableBuf.length - 1) {
      validViSyll = false;
      return;
    }

    String strVowel = "";
    for (int i = iCurPos; i < syllableBuf.length; ++i) {
      int idx = vnVowels.indexOf(syllableBuf[i]);
      if (idx == -1) break;

      strVowel += vnVowels.charAt((idx / 6) * 6);
      if (tone.getValue() == TONE.NO_TONE.getValue())
        tone = TONE.getTone(idx % 6);
    }

    Iterator iter = alMainVowels.iterator();
    while (iter.hasNext()) {
      String tempVowel = (String) iter.next();
      if (strVowel.startsWith(tempVowel)) {
        strMainVowel = tempVowel;
        iCurPos += tempVowel.length();
        return;
      }
    }
    validViSyll = false;
    return;
  }

  private void parseLastConsonant() {
    if (!validViSyll) return;
    if (iCurPos >= syllableBuf.length) {
      strLastConsonant = EMPTY;
      return ;
    }
    if (iCurPos + 2 < syllableBuf.length) {
      validViSyll = false;
      return;
    }

    String lastConsonant = vnLastConsonants(this.syllableBuf, iCurPos) ;
    if(lastConsonant == null) {
      strLastConsonant = EMPTY;
      if (iCurPos < syllableBuf.length) { validViSyll = false ; }
    } else {
      validViSyll = true;
      iCurPos += lastConsonant.length() ;
      this.strLastConsonant = lastConsonant ;
    }
  }

  final static public String VN_FIRST_CONSONANTS = 
      "b|c|ch|d|đ|g|gh|gi|h|k|kh|l|m|n|ng|ngh|nh|ph|q|r|s|t|th|tr|x|v";

  final static public String vnFirstConsonants(char[] buf, int pos) {
    switch(buf[pos]) {
    case 'b': return "b" ;
    case 'c': 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "ch" ; // ch
      return "c" ; 
    case 'd' : return "d";
    case 'đ' : return "đ";
    case 'g' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "gh" ; // ch
      else if(pos + 1 < buf.length && buf[pos + 1] == 'i') return "gi" ; // ch
      return "g" ;
    case 'h' : 
      return "h" ;
    case 'k' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "kh" ; // ch
      return "k" ;
    case 'l' : 
      return "l" ;
    case 'm' : 
      return "m" ;
    case 'n' : 
      if(pos + 2 < buf.length && buf[pos + 1] == 'g' && buf[pos + 1] == 'h') return  "ngh"; //ngh
      else if(pos + 1 < buf.length && buf[pos + 1] == 'g') return "ng" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "nh" ; //nh
      return "n" ;
    case 'p' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "ph" ; //ph
      return null ;
    case 'q' : return "q" ;
    case 'r' : return "r" ;
    case 's' : return "s" ;
    case 't' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "th" ; //th
      else if(pos + 1 < buf.length && buf[pos + 1] == 'r') return "tr" ; //tr
      return "t" ;
    case 'x' : return "x" ;
    case 'v' : return "v" ;
    }
    return null ;
  }

  final static public String VN_LAST_CONSONANTS = "c|ch|i|m|n|ng|nh|o|p|t|u|y";
  final static public String vnLastConsonants(char[] buf, int pos) {
    switch(buf[pos]) {
    case 'c': 
      if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "ch" ; // ch
      return "c" ;
    case 'i': return "i" ;
    case 'm': return "m" ;
    case 'n' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'g') return "ng" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'h') return "nh" ; //nh
      return "n" ;
    case 'o': return "o" ;
    case 'p': return "p" ;
    case 't': return "t" ;
    case 'u': return "u" ;
    case 'y': return "y" ;
    }
    return null ;
  }

  final static public String vnMainVowels(char[] buf, int pos) {
    switch(buf[pos]) {
    case 'a': return "a" ;
    case 'â': return "â" ;
    case 'ă': return "ă" ;
    case 'e': return "e" ;
    case 'ê': return "ê" ;
    case 'i' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'a') return "ia" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'ê') return "iê" ; //nh
      return "i" ;
    case 'o' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'o') return "oo" ; // ng
      return "o" ;
    case 'ơ': return "ơ" ;
    case 'ô': return "ô" ;
    case 'u' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'ô') return "uô" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'a') return "ua" ; //nh
      return "u" ;
    case 'ư' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'ơ') return "ươ" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'a') return "ưa" ; //nh
      return "ư" ;
    case 'y' : 
      if(pos + 1 < buf.length && buf[pos + 1] == 'a') return "ya" ; // ng
      else if(pos + 1 < buf.length && buf[pos + 1] == 'ê') return "yê" ; //nh
      return "y" ;
    }
    return null ;
  }
  private static void initArrayList(ArrayList al, String str) {
    StringTokenizer strTknr = new StringTokenizer(str, "|");
    while (strTknr.hasMoreTokens()) {
      al.add(strTknr.nextToken());
    }
  }

  static public void main(String[] args) throws Exception {
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    String vnSyllable = "nguyễn" ;
    String oSyllable  = "con" ;
    VnSyllable parser = new VnSyllable(vnSyllable) ;
    out.println("==> vn syllable " + parser.isValidVnSyllable());
  }

  static public class TONE {
    public static final TONE NO_TONE = new TONE(0);
    public static final TONE ACUTE = new TONE(1);
    public static final TONE ACCENT = new TONE(2);
    public static final TONE QUESTION = new TONE(3);
    public static final TONE TILDE = new TONE(4);
    public static final TONE DOT = new TONE(5);

    public static TONE getTone(int v) {
      switch (v) {
      case 0: return NO_TONE;
      case 1: return ACUTE;
      case 2: return ACCENT;
      case 3: return QUESTION;
      case 4: return TILDE;
      case 5: return DOT;
      default: return NO_TONE;
      }
    }

    public int getValue() { return value; }

    private TONE(int v) { value = v; }

    private int value;
  }
}