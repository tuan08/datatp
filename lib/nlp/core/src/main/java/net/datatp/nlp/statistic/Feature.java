package net.datatp.nlp.statistic;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Feature {
	private String feature ;
	private int    frequency ;

	public Feature() {} 
	
	public Feature(String feature, int freq) {
		this.feature = feature ;
		this.frequency = freq ;
	}
	
	public String getFeature() { return feature; }
	public void setFeature(String feature) { this.feature = feature; }
	
	public int getFrequency() { return frequency; }
	public void setFrequency(int frequency) { this.frequency = frequency; }
}
