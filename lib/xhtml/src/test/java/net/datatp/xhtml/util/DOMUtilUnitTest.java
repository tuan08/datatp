package net.datatp.xhtml.util;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import net.datatp.xhtml.parser.NekoParser;
import net.datatp.xhtml.util.DOMUtil;

public class DOMUtilUnitTest {
	static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
    "<HTML xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <HEAD>\n" +
    "    <TITLE>Hello world</TITLE>\n" +
    "    <META http-equiv='Refresh' Content='0; url=http://8xstudio.com/forum/index.php'>" +
    "  </HEAD>\n" +
    "  <BODY>\n" +
    "    <A id='AbsoluteLink' href='/static/link/ABCDE'>Hello</A>\n" +
    "    <BR/>\n" +
    "  </BODY>\n" +
    "</HTML>" ;
	
  @Test
  public void testDOMUtil() throws Exception {
    NekoParser parser = new NekoParser() ;
    Document doc = parser.parseNonWellForm(HTML) ;
    Assert.assertNull(DOMUtil.getBase(doc)) ;
    DOMUtil.createBase(doc, "http://vnexpress.net/kinh-doanh/test") ;
    Assert.assertEquals("http://vnexpress.net/kinh-doanh", DOMUtil.getBase(doc)) ;
    //new TNodePrinter(System.out).traverse(doc) ;
    Assert.assertEquals("Hello world", DOMUtil.getTitle(doc)) ;
    
    System.out.println("refresh url = " + DOMUtil.findRefreshMetaNodeUrl(doc));
    String meta = "<meta http-equiv='Refresh' content='0; url=http://8xstudio.com/forum/index.php'>";
    doc = parser.parseNonWellForm(meta) ;
    System.out.println("refresh url = " + DOMUtil.findRefreshMetaNodeUrl(doc));
    
    meta = "<meta http-equiv='Refresh' content='0; URL=index/en/index.php'>" ;
    doc = parser.parseNonWellForm(meta) ;
    System.out.println("refresh url = " + DOMUtil.findRefreshMetaNodeUrl(doc));
    
    meta = "<html><head></head><META HTTP-EQUIV='REFRESH' CONTENT='0; URL=http://www.bantincongnghe.com/bantincongnghe/modules.php?name=News'></html>";
    doc = parser.parseNonWellForm(meta) ;
    System.out.println("refresh url = " + DOMUtil.findRefreshMetaNodeUrl(doc));
  
    meta = "<meta http-equiv='Refresh' content='0; url=http://benhvienoto.vn/@forum/index.php'>";
    doc = parser.parseNonWellForm(meta) ;
    System.out.println("refresh url = " + DOMUtil.findRefreshMetaNodeUrl(doc));
  }
}