package net.datatp.nlp.statistic;


import org.junit.Assert;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFUnitTest {

  @Test
  public void run() throws Exception {
    IDFDictionaryBuilder builder = new IDFDictionaryBuilder() ;
    Feature[] doc1 = new Feature[] { new Feature("feature1", 1), new Feature("feature2", 2), new Feature("feature3", 3)  };
    Feature[] doc2 = new Feature[] { new Feature("feature2", 2), new Feature("feature3", 3), new Feature("feature4", 4)	 };
    Feature[] doc3 = new Feature[] { new Feature("feature3", 1)	 };
    builder.collect(doc1) ;
    builder.collect(doc2) ;
    builder.collect(doc3) ;

    IDFDictionary dict = builder.getIDFDictionary() ;

    IDFDictionary.IDFFeature feature1 = dict.getFeature("feature1") ;
    IDFDictionary.IDFFeature feature2 = dict.getFeature("feature2") ;
    IDFDictionary.IDFFeature feature3 = dict.getFeature("feature3") ;
    IDFDictionary.IDFFeature feature4 = dict.getFeature("feature4") ;

    Assert.assertEquals(3, dict.getDocumentCount()) ;

    Assert.assertEquals(1, feature1.getDocFrequency()) ;
    Assert.assertEquals(0, feature1.getId()) ;
    Assert.assertEquals(Math.log10((double)3/1), (double)feature1.getIdf(), 0.00000001) ;

    Assert.assertEquals(2, feature2.getDocFrequency()) ;
    Assert.assertEquals(Math.log10((double)3/2), (double)feature2.getIdf(), 0.00000001) ;

    Assert.assertEquals(3, feature3.getDocFrequency()) ;
    Assert.assertEquals(Math.log10((double)3/3), (double)feature3.getIdf(), 0.00000001) ;

    Assert.assertEquals(1, feature4.getDocFrequency()) ;
    Assert.assertEquals(3, feature4.getId()) ;
    Assert.assertEquals(Math.log10((double)3/1), (double)feature4.getIdf(), 0.00000001) ;

    dict.printTable(System.out) ;
  }
}
