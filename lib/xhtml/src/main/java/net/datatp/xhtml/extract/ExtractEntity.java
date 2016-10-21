package net.datatp.xhtml.extract;

import java.util.List;

import net.datatp.xhtml.XDocEntity;

public class ExtractEntity extends XDocEntity {
  private static final long serialVersionUID = 1L;
  
  final static public String TITLE       = "title";
  final static public String DESCRIPTION = "description";
  final static public String CONTENT     = "content";
  final static public String COMMENT     = "comment";
  
  public ExtractEntity(String name, String type) {
    super(name, type);
  }
  
  public void withTitle(String ... title) { field(TITLE, title); }
  
  public void withDescription(String ... desc) { field(DESCRIPTION, desc); }
  
  public void withContent(String ... content) { field(CONTENT, content); }
  
  public void withComment(String ... comment) { field(COMMENT, comment); }
  
  static public String toString(List<ExtractEntity> holder) {
    StringBuilder b = new StringBuilder();
    for(XDocEntity sel : holder) {
      b.append(sel.getFormattedText()).append("\n");
    }
    return b.toString();
  }
}