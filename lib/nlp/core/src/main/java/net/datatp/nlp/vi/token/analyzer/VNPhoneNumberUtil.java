package net.datatp.nlp.vi.token.analyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNPhoneNumberUtil {

  final static public String normalize(String string) {
    return normalize(string.toCharArray()) ;
  }

  final static public String normalize(char[] buf) {
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < buf.length; i++) {
      if(Character.isDigit(buf[i])) b.append(buf[i]) ;
    }
    return b.toString() ;
  }

  final static public String getMobileProvider(String string) {
    if(string.length() == 10) {
      if(string.startsWith("097")) return "Viettel" ;
      if(string.startsWith("098")) return "Viettel" ;
      if(string.startsWith("090")) return "MobiFone" ;
      if(string.startsWith("093")) return "MobiFone" ;
      if(string.startsWith("091")) return "VinaFone" ;
      if(string.startsWith("092")) return "VinaFone" ;
      if(string.startsWith("094")) return "VinaFone" ;
      if(string.startsWith("095")) return "SFone" ;
      if(string.startsWith("096")) return "EVNTelecom" ;
    } else if(string.length() == 11) {
      if(string.startsWith("0164")) return "Viettel" ;
      if(string.startsWith("0165")) return "Viettel" ;
      if(string.startsWith("0166")) return "Viettel" ;
      if(string.startsWith("0167")) return "Viettel" ;
      if(string.startsWith("0168")) return "Viettel" ;
      if(string.startsWith("0169")) return "Viettel" ;
      if(string.startsWith("0122")) return "MobiFone" ;
      if(string.startsWith("0126")) return "MobiFone" ;
      if(string.startsWith("0127")) return "VinaFone" ;
      if(string.startsWith("0128")) return "MobiFone" ;
      if(string.startsWith("0123")) return "VinaFone" ;
    }
    return null ;
  }

  final static public String getAreaCode(String normalizeString) {
    if(normalizeString.length() == 7 || normalizeString.length() == 8 ) {
      if(normalizeString.endsWith("000")) return null ;
      return null ;
    } else if(normalizeString.length() >= 9 && normalizeString.length() <= 11 ) {
      if(normalizeString.startsWith("076")) return "076" ;
      if(normalizeString.startsWith("064")) return "064" ;
      if(normalizeString.startsWith("781")) return "781" ;
      if(normalizeString.startsWith("281")) return "281" ;
      if(normalizeString.startsWith("240")) return "240" ;
      if(normalizeString.startsWith("241")) return "241" ;
      if(normalizeString.startsWith("075")) return "075" ;
      if(normalizeString.startsWith("650")) return "650" ;
      if(normalizeString.startsWith("056")) return "056" ;
      if(normalizeString.startsWith("651")) return "651" ;
      if(normalizeString.startsWith("062")) return "062" ;
      if(normalizeString.startsWith("780")) return "780" ;
      if(normalizeString.startsWith("026")) return "026" ;
      if(normalizeString.startsWith("071")) return "071" ;
      if(normalizeString.startsWith("511")) return "511" ;
      if(normalizeString.startsWith("051")) return "051" ;
      if(normalizeString.startsWith("050")) return "050" ;
      if(normalizeString.startsWith("023")) return "023" ;
      if(normalizeString.startsWith("061")) return "061" ;
      if(normalizeString.startsWith("067")) return "067" ;
      if(normalizeString.startsWith("059")) return "059" ;
      if(normalizeString.startsWith("019")) return "019" ;
      if(normalizeString.startsWith("351")) return "351" ;
      if(normalizeString.startsWith("04"))  return "04" ;
      if(normalizeString.startsWith("034")) return "034" ;
      if(normalizeString.startsWith("039")) return "039" ;
      if(normalizeString.startsWith("320")) return "320" ;
      if(normalizeString.startsWith("031")) return "031" ;
      if(normalizeString.startsWith("071")) return "071" ;
      if(normalizeString.startsWith("018")) return "018" ;
      if(normalizeString.startsWith("321")) return "321" ;
      if(normalizeString.startsWith("08"))  return "08" ;
      if(normalizeString.startsWith("058")) return "058" ;
      if(normalizeString.startsWith("077")) return "077" ;
      if(normalizeString.startsWith("060")) return "060" ;
      if(normalizeString.startsWith("023")) return "023" ;
      if(normalizeString.startsWith("025")) return "025" ;
      if(normalizeString.startsWith("020")) return "020" ;
      if(normalizeString.startsWith("063")) return "063" ;
      if(normalizeString.startsWith("072")) return "072" ;
      if(normalizeString.startsWith("350")) return "350" ;
      if(normalizeString.startsWith("038")) return "038" ;
      if(normalizeString.startsWith("030")) return "030" ;
      if(normalizeString.startsWith("068")) return "068" ;
      if(normalizeString.startsWith("210")) return "210" ;
      if(normalizeString.startsWith("057")) return "057" ;
      if(normalizeString.startsWith("052")) return "052" ;
      if(normalizeString.startsWith("510")) return "510" ;
      if(normalizeString.startsWith("055")) return "055" ;
      if(normalizeString.startsWith("033")) return "033" ;
      if(normalizeString.startsWith("053")) return "053" ;
      if(normalizeString.startsWith("079")) return "079" ;
      if(normalizeString.startsWith("022")) return "022" ;
      if(normalizeString.startsWith("066")) return "066" ;
      if(normalizeString.startsWith("036")) return "036" ;
      if(normalizeString.startsWith("280")) return "280" ;
      if(normalizeString.startsWith("037")) return "037" ;
      if(normalizeString.startsWith("054")) return "054" ;
      if(normalizeString.startsWith("073")) return "073" ;
      if(normalizeString.startsWith("074")) return "074" ;
      if(normalizeString.startsWith("027")) return "027" ;
      if(normalizeString.startsWith("070")) return "070" ;
      if(normalizeString.startsWith("211")) return "211" ;
      if(normalizeString.startsWith("029")) return "029" ;
    }
    return null ;
  }

  final static public String getAreaName(String string) {
    if(string.length() == 9 || string.length() == 10){
      if(string.startsWith("076")) return "An Giang" ;
      if(string.startsWith("064")) return "Bà Rịa Vũng Tàu" ;
      if(string.startsWith("781")) return "Bạc Liêu" ;
      if(string.startsWith("281")) return "Bắc Cạn" ;
      if(string.startsWith("240")) return "Bắc Giang" ;
      if(string.startsWith("241")) return "Bắc Ninh" ;
      if(string.startsWith("075")) return "Bến Tre" ;
      if(string.startsWith("650")) return "Bình Dương" ;
      if(string.startsWith("056")) return "Bình Định" ;
      if(string.startsWith("651")) return "Bình Phước" ;
      if(string.startsWith("062")) return "Bình Thuận" ;
      if(string.startsWith("780")) return "Cà Mau" ;
      if(string.startsWith("026")) return "Cao Bằng" ;
      if(string.startsWith("071")) return "Cần Thơ" ;
      if(string.startsWith("511")) return "Đà Nẵng" ;
      if(string.startsWith("051")) return "Đà Nẵng" ;
      if(string.startsWith("050")) return "Đắk Lắk / Đắk Nông" ;
      if(string.startsWith("023")) return "Điện Biên" ;
      if(string.startsWith("061")) return "Đồng Nai" ;
      if(string.startsWith("067")) return "Đồng Tháp" ;
      if(string.startsWith("059")) return "Gia Lai" ;
      if(string.startsWith("019")) return "Hà Giang" ;
      if(string.startsWith("351")) return "Hà Nam" ;
      if(string.startsWith("04"))  return "Hà Nội" ;
      if(string.startsWith("034")) return "Hà Tây" ;
      if(string.startsWith("039")) return "Hà Tĩnh" ;
      if(string.startsWith("320")) return "Hải Dương" ;
      if(string.startsWith("031")) return "Hải Phòng" ;
      if(string.startsWith("071")) return "Hậu Giang" ;
      if(string.startsWith("018")) return "Hòa Bình" ;
      if(string.startsWith("321")) return "Hưng Yên" ;
      if(string.startsWith("08"))  return "TP Hồ Chí Minh" ;
      if(string.startsWith("058")) return "Khánh Hòa" ;
      if(string.startsWith("077")) return "Kiên Giang" ;
      if(string.startsWith("060")) return "Kom Tum" ;
      if(string.startsWith("023")) return "Lai Châu" ;
      if(string.startsWith("025")) return "Lạng Sơn" ;
      if(string.startsWith("020")) return "Lào Cai" ;
      if(string.startsWith("063")) return "Lâm Đồng" ;
      if(string.startsWith("072")) return "Long An" ;
      if(string.startsWith("350")) return "Nam Định" ;
      if(string.startsWith("038")) return "Nghệ An" ;
      if(string.startsWith("030")) return "Ninh Bình" ;
      if(string.startsWith("068")) return "Ninh Thuận" ;
      if(string.startsWith("210")) return "Phú Thọ" ;
      if(string.startsWith("057")) return "Phú Yên" ;
      if(string.startsWith("052")) return "Quảng Bình" ;
      if(string.startsWith("510")) return "Quảng Nam" ;
      if(string.startsWith("055")) return "Quảng Ngãi" ;
      if(string.startsWith("033")) return "Quảng Ninh" ;
      if(string.startsWith("053")) return "Quảng Trị" ;
      if(string.startsWith("079")) return "Sóc Trăng" ;
      if(string.startsWith("022")) return "Sơn La" ;
      if(string.startsWith("066")) return "Tây Ninh" ;
      if(string.startsWith("036")) return "Thái Bình" ;
      if(string.startsWith("280")) return "Thái Nguyên" ;
      if(string.startsWith("037")) return "Thanh Hóa" ;
      if(string.startsWith("054")) return "Huế" ;
      if(string.startsWith("073")) return "Tiền Giang" ;
      if(string.startsWith("074")) return "Trà Vinh" ;
      if(string.startsWith("027")) return "Tuyên Quang" ;
      if(string.startsWith("070")) return "Vĩnh Long" ;
      if(string.startsWith("211")) return "Vĩnh Phúc" ;
      if(string.startsWith("029")) return "Yên Bái" ;	
    }
    return null ;
  }
}
