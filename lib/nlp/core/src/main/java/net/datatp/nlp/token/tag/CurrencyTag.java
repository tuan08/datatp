package net.datatp.nlp.token.tag;

import java.text.DecimalFormat;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CurrencyTag extends QuantityTag {
	final static public String TYPE = "currency" ;
	
	static DecimalFormat FORMATER = new DecimalFormat("##########.00 ") ;
	
	private double amount ;

	public CurrencyTag(double amount, String unit) {
		super(TYPE);
		this.amount = amount ;
		setUnit(unit) ;
	}
	
	public double getAmount() { return this.amount  ; }
	
	public String getTagValue() { 
		return FORMATER.format(amount)  + getUnit() ; }
}