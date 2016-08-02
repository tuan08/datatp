package net.datatp.crawler.site;

import java.util.HashMap;
import java.util.Map;

import net.datatp.xhtml.extract.CommentExtractor;
import net.datatp.xhtml.extract.ForumExtractor;
import net.datatp.xhtml.extract.MainContentExtractor;
import net.datatp.xhtml.extract.WDataExtractors;

public class AutoWDataExtractors {
  private Map<ExtractConfig.ExtractAuto, WDataExtractors> autoExtractors = new HashMap<>();

  public AutoWDataExtractors() {
    add(ExtractConfig.ExtractAuto.article, new WDataExtractors("article", new MainContentExtractor("article"), new CommentExtractor()));
    add(ExtractConfig.ExtractAuto.forum, new WDataExtractors("forum",   new ForumExtractor()));
  }

  void add(ExtractConfig.ExtractAuto type, WDataExtractors extractor) {
    autoExtractors.put(type, extractor);
  }
  
  public WDataExtractors getExtractor(ExtractConfig.ExtractAuto type) {
    WDataExtractors foundExtractor = autoExtractors.get(type);
    if(foundExtractor == null) {
      throw new RuntimeException("Cannot find the extractor " + type);
    }
    return foundExtractor;
  }
}
