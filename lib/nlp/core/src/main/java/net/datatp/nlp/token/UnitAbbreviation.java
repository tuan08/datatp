package net.datatp.nlp.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnitAbbreviation {
	final static public UnitAbbreviation[] VND = {
		new UnitAbbreviation("vnd",  "vnd", "đồng"),
		new UnitAbbreviation("vnđ",  "vnd", "đồng"),
		new UnitAbbreviation("đồng", "vnd", "đồng"),
		new UnitAbbreviation("dong", "vnd", "đồng"),
		new UnitAbbreviation("đ",    "vnd", "đồng"),
		new UnitAbbreviation("₫",    "vnd", "đồng"),
		new UnitAbbreviation("đ/m2", "vnd", "đồng/m2"),
		new UnitAbbreviation("₫/m2", "vnd", "đồng/m2"),
		new UnitAbbreviation("đ/km", "vnd", "đồng/km"),
		new UnitAbbreviation("₫/km", "vnd", "đồng/km"),
		
		new UnitAbbreviation("ngàn",  "vnd", "1 ngàn đồng"),
		new UnitAbbreviation("ngan",  "vnd", "1 ngàn đồng"),
		new UnitAbbreviation("triệu", "vnd", "1 triệu đồng"),
		new UnitAbbreviation("trieu", "vnd", "1 triệu đồng"),
		new UnitAbbreviation("tr",    "vnd", "1 triệu đồng"),
		new UnitAbbreviation("tỷ",    "vnd", "1 tỷ đồng"),
		new UnitAbbreviation("ty",    "vnd", "1 tỷ đồng")
	};
	
	final static public UnitAbbreviation[] USD = {
		new UnitAbbreviation("usd",  "usd", "dollard"),
		new UnitAbbreviation("$",    "usd", "dollard"),
		new UnitAbbreviation("$/m2",    "usd", "dollard/m2"),
	};
	
	final static public UnitAbbreviation[] SI_LENGHT = {
		new UnitAbbreviation("km",  "length", "1000 m"),
		new UnitAbbreviation("m",   "length", "1 m"),
		new UnitAbbreviation("dm",  "length", "1/10 m"),
		new UnitAbbreviation("cm",  "length", "1/100 m"),
		new UnitAbbreviation("mm",  "length", "1/1000 m"),
		new UnitAbbreviation("µm",  "length", "1/1000000 m"),
		new UnitAbbreviation("nm",  "length", "1/1000000000 m"),
	};
	
	final static public UnitAbbreviation[] US_LENGHT = {
		new UnitAbbreviation("mi",  "length", "1 mile"),
		new UnitAbbreviation("ft.",  "length", "1 foot"),
		new UnitAbbreviation("in",  "length", "1 inch"),
		new UnitAbbreviation("inch",  "length", "1 inch"),
		new UnitAbbreviation("n.m",  "length", "nautical miles"),
	};
	
	final static public UnitAbbreviation[] SI_SUPERFICIE = {
		new UnitAbbreviation("km2",  "superficie", "1 km2"),
		new UnitAbbreviation("m2",   "superficie", "1 m2"),
		new UnitAbbreviation("dm2",  "superficie", "1 dm2"),
		new UnitAbbreviation("cm2",  "superficie", "1 cm2"),
		new UnitAbbreviation("mm2",  "superficie", "1 mm2"),
		new UnitAbbreviation("µm2",  "superficie", "1 µm2"),
		new UnitAbbreviation("nm2",  "superficie", "1 nm2"),
	
		new UnitAbbreviation("sào",  "superficie", "360m2"),
		new UnitAbbreviation("mẫu",  "superficie", "10 sào"),
		new UnitAbbreviation("ha",  "superficie",  "1 hecta(10000m2)"),
		new UnitAbbreviation("dpi",  "superficie", "dot per inch"),
	};
	
	final static public UnitAbbreviation[] BINARY = {
		new UnitAbbreviation("b",   "binary", "1 bit/byte"),
		new UnitAbbreviation("tb",  "binary", "1 tera bytes"),
		new UnitAbbreviation("gb",  "binary", "1 giga bytes"),
		new UnitAbbreviation("mb",  "binary", "1 mega bytes"),
		new UnitAbbreviation("kb",  "binary", "1 kilo bytes"),
	};
	
	final static public UnitAbbreviation[] WEIGHT = {
		new UnitAbbreviation("kg",  "weight", "1 kg"),
		new UnitAbbreviation("g" ,  "weight", "1 gram"),
		new UnitAbbreviation("gr" ,  "weight", "1 gram"),
		new UnitAbbreviation("mg" , "weight", "1 milli gram"),
		
		new UnitAbbreviation("t" ,   "weight", "1 tấn"),
		new UnitAbbreviation("tấn" , "weight", "1 tấn"),
		new UnitAbbreviation("tạ" ,  "weight", "1 tạ"),
	};
	 
	final static public UnitAbbreviation[] VOLUME = {
		new UnitAbbreviation("m3",  "volume", "1 m3"),
		new UnitAbbreviation("cm3", "volume", "1 cm3"),
		new UnitAbbreviation("cc",  "volume", "1 cm3"),
		new UnitAbbreviation("mm3", "volume", "1 mm3"),
		new UnitAbbreviation("lít", "volume", "1 litter"),
		new UnitAbbreviation("l",   "volume", "1 litter"),
		new UnitAbbreviation("ml",  "volume", "1 ml"),
	};
	
	final static public UnitAbbreviation[] SPEED = {
		new UnitAbbreviation("ghz",  "speed", "1 giga herzt per s"),
		new UnitAbbreviation("mhz",  "speed", "1 mega herzt per s"),
		new UnitAbbreviation("khz",  "speed", "1 kilo herzt per s"),
		new UnitAbbreviation("hz",   "speed", "1 herzt per s"),
		
		new UnitAbbreviation("rpm",   "speed", "1 round per minute"),
		
		new UnitAbbreviation("mbps",   "speed", "1 mega bit per s"),
	};
	
	final static public UnitAbbreviation[] ENERGY = {
		new UnitAbbreviation("w",  "energy", "1 watt"),
		new UnitAbbreviation("kw", "energy", "KiloWatts (1000 watts = 1 kW)"),
		new UnitAbbreviation("mw", "energy", "Mega watt"),
		new UnitAbbreviation("gw", "energy", "Giga watt"),
		new UnitAbbreviation("tw", "energy", "Tera watt"),
		
		new UnitAbbreviation("a",  "energy", "Ampere (Volt-Amperes or Current)"),
		new UnitAbbreviation("ma",  "energy", "milli Ampere"),
		
		new UnitAbbreviation("v",  "energy", "1 volt(hertz)"),
		new UnitAbbreviation("kv", "energy", "1 kilo volt"),
		
		new UnitAbbreviation("va","energy", "1 Volt Amperes"),
		new UnitAbbreviation("kva","energy", "1 Kilo Volt Amperes"),
		
		new UnitAbbreviation("vac","energy", "Volt AC"),
	};
	
	final static public UnitAbbreviation[] TEMP = {
		new UnitAbbreviation("c",  "temp", "1 degree"),
		new UnitAbbreviation("ºc",  "temp", "1 degree"),
		new UnitAbbreviation("f",  "temp", "1 farenheight")
	};
	
	final static public UnitAbbreviation[] TIME = {
		new UnitAbbreviation("h",  "time", "1 hour"),
		new UnitAbbreviation("g",  "time", "1 hour"),
		new UnitAbbreviation("giờ",  "time", "1 hour"),
		new UnitAbbreviation("ngày",  "time", "1 day"),
		new UnitAbbreviation("tháng",  "time", "1 month"),
		new UnitAbbreviation("năm",  "time", "1 year"),
	};
	
	final static public UnitAbbreviation[] OTHER = {
		new UnitAbbreviation("mp",  "other", "Mega pixel"),
		new UnitAbbreviation("pixcel",  "other", "1 pixel"),
	};
	
	final static public UnitAbbreviation[] ALL =
		merge(VND, USD, SI_LENGHT, US_LENGHT, SI_SUPERFICIE, BINARY, WEIGHT, VOLUME, SPEED, ENERGY, TEMP, TIME, OTHER) ;
	
	final public String abbr ;
	final public String type ;
	final public String desc ;
	
	public UnitAbbreviation(String abbr, String type, String desc) {
		this.abbr = abbr ;
		this.type = type ;
		this.desc = desc ;
	}
	
	static public String[] getUnits(UnitAbbreviation[] arrays) {
		HashSet<String> holder = new HashSet<String>() ;
		for(UnitAbbreviation sel : UnitAbbreviation.ALL) {
			holder.add(sel.abbr) ;
		}
		String[] unit = holder.toArray(new String[holder.size()]) ;
		Arrays.sort(unit, new StringLengthComparator()) ;
		return unit ;
	}
	
	static public UnitAbbreviation[] merge(UnitAbbreviation[] ... arrays) {
		List<UnitAbbreviation> holder = new ArrayList<UnitAbbreviation>() ;
		for(UnitAbbreviation[] sel : arrays) {
			for(UnitAbbreviation usel : sel) holder.add(usel) ;
		}
		return holder.toArray(new UnitAbbreviation[holder.size()]) ;
	}
	
	static public class StringLengthComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
	    return s2.length() - s1.length();
    }
	}
}