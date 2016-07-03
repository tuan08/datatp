package net.datatp.xhtml.dom;

import net.datatp.xhtml.dom.TNodeXPath;

import org.junit.Assert;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNodeXPathUnitTest {
  @Test
  public void test() throws Exception {
    String XPATH1 = "body[0]/div[0]/div[0]/span[0]/text[0]" ;
    String XPATH2 = "body[0]/div[0]/div[0]/span[1]/text[0]" ;

    TNodeXPath xpath1 = new TNodeXPath(XPATH1) ;
    TNodeXPath xpath2 = new TNodeXPath(XPATH2) ;

    Assert.assertTrue(xpath1.equals(xpath1)) ;
    Assert.assertTrue(xpath1.equalsIgnoreIndex(xpath2)) ;

    //TODO: Test all the public method of the TNodeXPath class
  }
}
