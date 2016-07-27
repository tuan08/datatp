package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test ;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.XPathRepetions;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class XPathStructureAnanlyzerUnitTest {
  @Test
  public void test() throws Exception {
    String anchorText = "Việt Nam luôn sẵn sàng mọi biện pháp duy trì hòa bình ở Biển Đông";
    String url = "http://vnexpress.net/tin-tuc/the-gioi/viet-nam-luon-san-sang-moi-bien-phap-duy-tri-hoa-binh-o-bien-dong-3436403.html";
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    Document doc = wPageData.createJsoupDocument();
    doc.traverse(new NodeCleanerVisitor(NodeCleaner.EMPTY_NODE_CLEANER, NodeCleaner.IGNORE_NODE_CLEANER));
    XPathStructure structure = new XPathStructure(doc);
    XPathRepetions repetions = structure.getXPathRepetions();
    
    System.out.println(repetions.getFormattedText());
    
    Elements elements = doc.select("[tag:text=text:small]");
    for(int i = 0; i < elements.size(); i++) {
      System.out.println(elements.get(i).text());
    }
    
    System.out.println("\n------------------------------\n");
    MainContentExtractor extractor = new MainContentExtractor();
    XPathExtract[] xpathExtract = extractor.extract(structure);
      
    System.out.println(XPathExtract.getFormattedText(xpathExtract));
  }
}