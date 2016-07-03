package net.datatp.xhtml.dom.extract;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ExtractContent {
  private List<ExtractBlock> extracts = new ArrayList<ExtractBlock>() ;

  public ExtractContent() { }

  public void add(ExtractBlock block) {
    if(block == null) return  ;
    extracts.add(block); 
  }

  public List<ExtractBlock> getExtractBlocks() { return this.extracts ; }

  public ExtractBlock getExtractBlock(String name) {
    for(int i = 0; i < extracts.size(); i++) {
      ExtractBlock block = extracts.get(i) ;
      if(name.equals(block.getBlockName())) return block ;
    }
    return null ;
  }

  public void dump(PrintStream out) {
    for(int i = 0; i < extracts.size(); i++) {
      ExtractBlock block = extracts.get(i) ;
      out.println("BLOCK      : "  + block.getBlockName());
      out.println("TAGS       : "  + StringUtil.joinStringArray(block.getTags())) ;
      out.println("TITLE      : "  + block.getTitle());
      out.println("DESCRIPTION: "  + block.getDescription());
      out.println("BODY       :\n" + block.getContent());
      //block.dumpNodes(out) ;
    }
  }
}