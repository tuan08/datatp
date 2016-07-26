package net.datatp.xhtml.dom;

import net.datatp.xhtml.parser.JSoupParser;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TDocument {
  private String   xhtml ;
  private String   anchorText ;
  private String   url   ;
  private TNode    root ;

  public TDocument(String anchorText, String url, String xhtml) {
    this.anchorText = anchorText ;
    this.url = url ;
    this.xhtml = xhtml ;
    root = JSoupParser.INSTANCE.toTNode(xhtml) ;
  }

  public String getAnchorText() { return anchorText; }
  public void setAnchorText(String anchorText) { this.anchorText = anchorText; }

  public String getUrl() { return url; }
  public void   setUrl(String url) { this.url = url; }

  public String getXHTML() { return this.xhtml ; }

  public TNode getRoot() { return root; }

  public void setRoot(TNode root) { this.root = root; }
}