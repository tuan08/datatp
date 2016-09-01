package net.datatp.crawler.site;

import java.util.HashMap;
import java.util.Map;

import net.datatp.xhtml.extract.CommentExtractor;
import net.datatp.xhtml.extract.ForumExtractor;
import net.datatp.xhtml.extract.MainContentExtractor;
import net.datatp.xhtml.extract.WDataExtractor;

public class AutoWDataExtractors {
  private Map<ExtractConfig.ExtractType, WDataExtractor> autoExtractors = new HashMap<>();

  public AutoWDataExtractors() {
    add(ExtractConfig.ExtractType.article, new MainContentExtractor("article"));
    add(ExtractConfig.ExtractType.forum,   new ForumExtractor());
    add(ExtractConfig.ExtractType.comment, new CommentExtractor());
  }

  void add(ExtractConfig.ExtractType type, WDataExtractor extractor) {
    autoExtractors.put(type, extractor);
  }
  
  public WDataExtractor getExtractor(ExtractConfig.ExtractType type) {
    WDataExtractor foundExtractor = autoExtractors.get(type);
    if(foundExtractor == null) {
      throw new RuntimeException("Cannot find the extractor " + type);
    }
    return foundExtractor;
  }
}
