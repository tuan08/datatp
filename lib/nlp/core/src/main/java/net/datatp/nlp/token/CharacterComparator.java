package net.datatp.nlp.token;

import net.datatp.nlp.vi.VietnameseUtil;

public interface CharacterComparator {
  final static CharacterComparator DEFAULT = new DefaultCharacterComparator() ;
  final static CharacterComparator NO_VN_ACCENT = new NoVNAccentCharacterComparator() ;
  
  public int compare(char c1, char c2) ;
  
  static public class DefaultCharacterComparator implements CharacterComparator {
    public int compare(char c1, char c2) {
      c1 = Character.toLowerCase(c1) ;
      c2 = Character.toLowerCase(c2) ;
      return c1 - c2;
    }
  }
  
  static public class NoVNAccentCharacterComparator implements CharacterComparator {
    public int compare(char c1, char c2) {
      c1 = VietnameseUtil.removeVietnameseAccent(Character.toLowerCase(c1)) ;
      c2 = VietnameseUtil.removeVietnameseAccent(Character.toLowerCase(c2)) ;
      return c1 - c2;
    }
  }
}
