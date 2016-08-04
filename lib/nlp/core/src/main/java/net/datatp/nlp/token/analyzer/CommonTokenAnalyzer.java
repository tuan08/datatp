package net.datatp.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.UnitAbbreviation;
import net.datatp.nlp.token.tag.CharacterTag;
import net.datatp.nlp.token.tag.DigitTag;
import net.datatp.nlp.token.tag.EmailTag;
import net.datatp.nlp.token.tag.NumberTag;
import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.nlp.token.tag.URLTag;
import net.datatp.nlp.token.tag.WordTag;
import net.datatp.nlp.vi.VietnameseUtil;
import net.datatp.util.text.NumberUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CommonTokenAnalyzer extends TokenAnalyzer {
  final static public CommonTokenAnalyzer INSTANCE = new CommonTokenAnalyzer() ;

  static private String[] suffix ;
  static {
    suffix = UnitAbbreviation.getUnits(UnitAbbreviation.ALL) ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>(tokens.length + 25) ;
    for(IToken sel : tokens) {
      if(sel.getNormalizeForm().length() == 0) continue ;
      if(sel instanceof TokenCollection) {
        holder.add(sel) ;
      } else {
        sel.add(createTag(sel.getNormalizeForm(), sel.getOriginalForm())) ;
        holder.add(sel) ;
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }

  static public TokenTag createTag(String nform, String oform) {
    char[] characters = nform.toCharArray();
    byte viletter = 0, letter = 0, digit = 0, colon = 0, dot = 0, 
        comma = 0, fslash = 0, bslash = 0, dash = 0, percent = 0, at = 0, 
        nline = 0, space = 0, other = 0 ;
    for (char sel : characters) {
      if(sel >= 'a' && sel <= 'z') letter++;
      else if(sel >= '0' && sel <= '9') digit++;
      else if(sel == '.')  dot++;
      else if(sel == ',')  comma++;
      else if(sel == ':')  colon++;
      else if(sel == '%')  percent++;
      else if(sel == '/')  fslash++;
      else if(sel == '-')  dash++;
      else if(sel == '@')  at++;
      else if(sel == '\\') bslash++;
      else if(sel == '\n') nline++;
      else if(sel == ' ')  space++;
      else if(VietnameseUtil.isVietnameseCharacter(sel)) viletter++;
      else if(Character.isLetter(sel)) letter++;
      else other++ ;
    }
    int letterCount  = (letter + viletter + space) ;
    if(digit == characters.length) {
      return new DigitTag(nform) ;
    } else if(letterCount == characters.length) {
      return WordTag.WLETTER ;
    } else if(digit + dot + comma == characters.length && 
        Character.isDigit(characters[characters.length - 1])) {
      Double value = NumberUtil.parseRealNumber(characters) ;
      if(value != null) return new NumberTag(value) ;
    } else if(dot > 0) {
      boolean endWithKnowDomain = URLTag.isEndWithKnownDomain(nform) ;
      if(endWithKnowDomain && at == 1) return new EmailTag(oform) ;
      if(endWithKnowDomain) return new URLTag(oform) ;
      if(URLTag.isStartWithKnownProtocol(nform)) return new URLTag(oform) ;
    } 

    List<CharacterTag.CharDescriptor> holder = new ArrayList<CharacterTag.CharDescriptor>() ;
    if(letterCount > 0) holder.add(new CharacterTag.CharDescriptor('l', (byte)letterCount)) ;
    if(viletter > 0) holder.add(new CharacterTag.CharDescriptor('V', viletter)) ;
    if(digit > 0) holder.add(new CharacterTag.CharDescriptor('d', digit)) ;
    if(nline > 0) holder.add(new CharacterTag.CharDescriptor('n', nline)) ;
    if(dot > 0)  holder.add(new CharacterTag.CharDescriptor('.', dot)) ;
    if(comma > 0)  holder.add(new CharacterTag.CharDescriptor(',', comma)) ;
    if(colon > 0)  holder.add(new CharacterTag.CharDescriptor(':', colon)) ;
    if(percent > 0) holder.add(new CharacterTag.CharDescriptor('%', percent)) ;
    if(fslash > 0) holder.add(new CharacterTag.CharDescriptor('/', fslash)) ;
    if(dash > 0)  holder.add(new CharacterTag.CharDescriptor('-', dash)) ;
    if(bslash > 0) holder.add(new CharacterTag.CharDescriptor('\\', bslash)) ;

    String foundSuffix = null ;
    if(digit > 0) {
      for(String selSuffix : suffix) {
        if(nform.endsWith(selSuffix)) {
          foundSuffix = selSuffix ;
          break ;
        }
      }
    }
    return new CharacterTag(holder, foundSuffix) ;
  }
}