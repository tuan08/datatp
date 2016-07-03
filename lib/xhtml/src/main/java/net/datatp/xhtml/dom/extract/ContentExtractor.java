package net.datatp.xhtml.dom.extract;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.selector.TextLengthSelector;
import net.datatp.xhtml.dom.tagger.JunkTextBlockTagger;
import net.datatp.xhtml.dom.tagger.LinkBlockTagger;
import net.datatp.xhtml.dom.tagger.TitleBlockTagger;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class ContentExtractor {
  static String[] DETAIL_JUNK_TEXT_TAG = {
      JunkTextBlockTagger.BLOCK_TEXT_JUNK, 
      LinkBlockTagger.BLOCK_LINK_ACTION, 
      LinkBlockTagger.BLOCK_LINK_RELATED
  } ;

  static String[] OTHER_JUNK_TEXT_TAG = {
      JunkTextBlockTagger.BLOCK_TEXT_JUNK, 
      LinkBlockTagger.BLOCK_LINK_ACTION, 
  } ;

  abstract protected ExtractContent extractDetail(TDocument tdoc) ;
  abstract protected ExtractContent extractList(TDocument tdoc) ;
  abstract protected ExtractContent extractOther(TDocument tdoc) ;

  public ExtractContent extract(TDocument tdoc) {
    String atext = tdoc.getAnchorText() ;
    if(atext == null || atext.length() < 15) { // try extract list first
      ExtractContent list = extractList(tdoc) ;
      if(list != null) return list ;
      ExtractContent detail = extractDetail(tdoc) ;
      if(detail != null) return detail ;
    } else {
      ExtractContent detail = extractDetail(tdoc) ;
      if(detail != null) return detail ;
      ExtractContent list = extractList(tdoc) ;
      if(list != null) return list ;
    }
    return extractOther(tdoc) ;
  }

  protected TNode findBlockWithTitle(TNode node) {
    TNode ancestor = node;
    int backLevel = 0 ;
    while(ancestor != null && backLevel < 4) {
      if(ancestor.hasDescendantTag(TitleBlockTagger.TITLE_CANDIDATE)) {
        return ancestor ;
      }
      ancestor = ancestor.getParent() ;
      backLevel++ ;
    }
    return null ;
  }

  protected ExtractBlock createDetail(String tag, TDocument tdoc, TNode mainBlock) {
    String title   = null ;
    String desc    = null ;
    StringBuilder contentB = new StringBuilder() ;
    TNode[] nodes = mainBlock.select(new TextLengthSelector(0, 100000)) ;
    for(int i = 0; i < nodes.length; i++) {
      TNode node = nodes[i] ;
      if(title == null && node.hasTag(TitleBlockTagger.TITLE_CANDIDATE)) {
        title = node.getTextContent() ;
      } else {
        TNode[] textNode = node.select(new TextLengthSelector(0, 100000)) ;
        for(int j = 0; j < textNode.length; j++) {
          if(textNode[j].hasAncestorTag(DETAIL_JUNK_TEXT_TAG)) continue ;
          contentB.append(textNode[j].getTextContent()).append("\n") ;
        }
      }
    }
    if(title == null) {
      title = tdoc.getAnchorText() ;
    }
    ExtractBlock block = new ExtractBlock("mainContent") ;
    block.setTitle(title, null) ;
    block.setContent(contentB.toString(), null);
    block.setTags(tag, "content:detail") ;
    return block ;
  }

  protected ExtractContent extractOther(String tag, TDocument tdoc) {
    TextLengthSelector tselector = new TextLengthSelector(0, 10000) ;
    TNode[] nodes = tdoc.getRoot().select(tselector) ;
    int titlePos = this.findFirstTitleCandidate(nodes) ;
    int start = 0 ;
    if(titlePos >= 0) start = titlePos + 1 ;

    String title = tdoc.getAnchorText() ;
    if(titlePos >= 0) title = nodes[titlePos].getNodeValue() ;
    boolean detailDoc = false ;
    String[] ignoreTagBlock = DETAIL_JUNK_TEXT_TAG ;
    if(!StringUtil.isEmpty(title) && title.length() > 25) {
      detailDoc = true ;
      ignoreTagBlock = OTHER_JUNK_TEXT_TAG ;
    }

    StringBuilder b = new StringBuilder() ;
    for(int i = start; i < nodes.length; i++) {
      TNode sel = nodes[i] ;
      if(sel.hasAncestorTag(ignoreTagBlock)) continue ;
      b.append(sel.getTextContent()).append("\n") ;
    }

    ExtractBlock block = new ExtractBlock("mainContent") ;
    block.setTitle(title, null) ;
    block.setContent(b.toString(), null);
    if(detailDoc) block.setTags(tag, "content:detail") ;
    else block.setTags(tag, "content:unknown-type") ;
    ExtractContent extractContent = new ExtractContent();
    extractContent.add(block) ;
    return extractContent;
  }

  private int findFirstTitleCandidate(TNode[] nodes) {
    for(int i = 0; i < nodes.length; i++) {
      if(nodes[i].hasTag(TitleBlockTagger.TITLE_CANDIDATE)) return i ;
    }
    return -1 ;
  }
}
