package net.datatp.webcrawler.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.extract.DocumentExtractor;
import net.datatp.xhtml.dom.extract.ExtractBlock;
import net.datatp.xhtml.dom.extract.ExtractContent;

public class XHTMLContentExtractor {
  private Map<String, DocumentExtractor.Type> hints = new HashMap<String, DocumentExtractor.Type>() ;
  private DocumentExtractor extractor = new DocumentExtractor() ;

  public XHTMLContentExtractor() {
    hints.put("content:article",    DocumentExtractor.Type.article) ;
    hints.put("content:blog",       DocumentExtractor.Type.blog) ;
    hints.put("content:forum",      DocumentExtractor.Type.forum) ;
    hints.put("content:product",    DocumentExtractor.Type.product) ;
    hints.put("content:classified", DocumentExtractor.Type.classified) ;
    hints.put("content:job",        DocumentExtractor.Type.job) ;
    hints.put("content:other",      DocumentExtractor.Type.other) ;
    hints.put("content:ignore",     DocumentExtractor.Type.ignore) ;
  }

  public ExtractContent extract(XhtmlDocument xdoc, TDocument tdoc, String hint) {
    DocumentExtractor.Type hintType = hints.get(hint) ;
    ExtractContent extractContent = extractor.extract(hintType, tdoc) ;
    List<ExtractBlock> extractBlocks = extractContent.getExtractBlocks() ;
    for(int i = 0; i < extractBlocks.size(); i++) {
      ExtractBlock block = extractBlocks.get(i) ;
      String blockName = block.getBlockName() ;

      xdoc.addTag(block.getTags()) ;
    }
    return extractContent ;
  }
}