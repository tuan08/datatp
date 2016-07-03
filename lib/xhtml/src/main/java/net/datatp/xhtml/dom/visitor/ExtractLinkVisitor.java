package net.datatp.xhtml.dom.visitor;

import java.util.ArrayList;
import java.util.List;

import net.datatp.util.text.CosineSimilarity;
import net.datatp.xhtml.XhtmlLink;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeVisitor;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class ExtractLinkVisitor implements TNodeVisitor {
  private List<XhtmlLink> linkHolder = new ArrayList<XhtmlLink>();

  public int onVisit(TNode node) {
    String tag = node.getNodeName() ;
    if("a".equals(tag)) {
      String href = node.getAttribute("href") ;
      if(href != null && !href.isEmpty()) {
        String anchorText = node.getTextContent().trim() ;
        if(anchorText.length() > 80) {
          String title = node.getAttribute("title") ;
          if(title != null && title.length() > 0 && title.length() < anchorText.length()) {
            String substring = anchorText.substring(0, title.length()) ;
            if(CosineSimilarity.INSTANCE.similarity(title, substring) > 0.8) {
              anchorText = title ;
            }
          }
        } 
        linkHolder.add(new XhtmlLink(anchorText, href)) ;
        return SKIP ;
      }
    }
    return CONTINUE ;
  }

  public List<XhtmlLink> getLinks() { return linkHolder ; }
}