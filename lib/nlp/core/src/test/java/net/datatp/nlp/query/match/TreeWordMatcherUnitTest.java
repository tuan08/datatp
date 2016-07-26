package net.datatp.nlp.query.match;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.nlp.token.WordTokenizer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TreeWordMatcherUnitTest {
  @Test
  public void testConstruction() throws Exception {
    TreeWordMatcher tree = new TreeWordMatcher() ;
    tree.addWord("test") ;
    tree.addWord("kinh") ;
    tree.addWord("kinh doanh") ;
    tree.addWord("bat dong san") ;
    tree.addWord("kinh doanh mua ban") ;
    tree.dump(0) ;

    System.out.println(tree.matches(new WordTokenizer("kinh").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh mua").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh mua ban").allTokens(), 0)) ;
    
    Assert.assertEquals("kinh", tree.matches(new WordTokenizer("kinh").allTokens(), 0).getWord()) ;
    Assert.assertEquals("kinh doanh", tree.matches(new WordTokenizer("kinh doanh").allTokens(), 0).getWord()) ;
    Assert.assertEquals("kinh doanh", tree.matches(new WordTokenizer("kinh doanh mua").allTokens(), 0).getWord()) ;
    Assert.assertEquals("kinh doanh mua ban", tree.matches(new WordTokenizer("kinh doanh mua ban").allTokens(), 0).getWord()) ;
    Assert.assertEquals("bat dong san", tree.matches(new WordTokenizer("bat dong san").allTokens(), 0).getWord()) ;
  }
}