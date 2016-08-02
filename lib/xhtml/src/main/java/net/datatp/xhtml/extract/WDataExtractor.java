package net.datatp.xhtml.extract;

import net.datatp.xhtml.extract.entity.ExtractEntity;

public interface WDataExtractor {
  public  WDataExtract extract(WDataExtractContext context) ;
  
  public ExtractEntity extractEntity(WDataExtractContext context) ;
}
