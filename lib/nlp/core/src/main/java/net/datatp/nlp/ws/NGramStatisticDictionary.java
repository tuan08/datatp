package net.datatp.nlp.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NGramStatisticDictionary {
  private List<NGramStatistic> unigram = new ArrayList<NGramStatistic>();
  private List<NGramStatistic> bigram = new ArrayList<NGramStatistic>();
  private Map<String, NGramStatistic> unigramIndex ;
  private Map<String, NGramStatistic> unigramNormIndex ;
  private Map<String, NGramStatistic> bigramIndex ;
  private Map<String, NGramStatistic> bigramNormIndex ;
  
  public NGramStatisticDictionary() { }

  public NGramStatisticDictionary(List<NGramStatistic> unigram, List<NGramStatistic> bigram) {
    this.unigram = unigram;
    this.bigram = bigram;
  }

  public List<NGramStatistic> getUnigram() { return unigram;}

  public NGramStatistic getUnigram(String word) {
  	return unigramIndex.get(word) ;
  }

  public NGramStatistic getUnigramNormalize(String word) { 
  	return unigramNormIndex.get(word) ;
  }

  public void setUnigram(List<NGramStatistic> unigram) {
    this.unigram = unigram;
  }

  public List<NGramStatistic> getBigram() { return bigram; }

  public NGramStatistic getBigram(String word) {
    return this.bigramIndex.get(word) ;
  }

  public NGramStatistic getBigramNormalize(String word) {
  	return this.bigramNormIndex.get(word) ;
  }

  public void setBigram(List<NGramStatistic> bigram) { this.bigram = bigram; }

  public void buildIndex() {
    unigramIndex = new HashMap<String, NGramStatistic>() ;
    unigramNormIndex = new HashMap<String, NGramStatistic>() ;
    for(NGramStatistic sel : unigram) {
    	addEntry(unigramIndex, sel.getWord(), sel) ;
    	addEntry(unigramNormIndex, sel.getNWord(), sel) ;
    }
    bigramIndex = new HashMap<String, NGramStatistic>() ;
    bigramNormIndex = new HashMap<String, NGramStatistic>() ;
    for(NGramStatistic sel : bigram) {
    	addEntry(bigramIndex, sel.getWord(), sel) ;
    	addEntry(bigramNormIndex, sel.getNWord(), sel) ;
    }
  }
  
  private void addEntry(Map<String, NGramStatistic> index, String word, NGramStatistic entry) {
  	NGramStatistic exist = index.get(word) ;
  	if(exist != null) {
  		if(entry.getDistribution() != exist.getDistribution() &&
  			 entry.getNWordDistribution() != exist.getNWordDistribution()) {
  			throw new RuntimeException("Duplicate " + word + " entry") ;
  		}
  	}
  	index.put(word, entry) ;
  }
}