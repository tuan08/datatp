package net.datatp.crawler.site;

import java.util.HashMap;
import java.util.Map;

import net.datatp.xhtml.xpath.CommentExtractor;
import net.datatp.xhtml.xpath.ForumExtractor;
import net.datatp.xhtml.xpath.MainContentExtractor;
import net.datatp.xhtml.xpath.WDataExtractor;

public class AutoWDataExtractors {
  private Map<ExtractConfig.ExtractAuto, WDataExtractor> autoExtractors = new HashMap<>();

  public AutoWDataExtractors() {
    add(ExtractConfig.ExtractAuto.article, new WDataExtractor("article", new MainContentExtractor("article"), new CommentExtractor()));
    add(ExtractConfig.ExtractAuto.forum, new WDataExtractor("forum",   new ForumExtractor()));
  }

  void add(ExtractConfig.ExtractAuto type, WDataExtractor extractor) {
    autoExtractors.put(type, extractor);
  }
  
  public WDataExtractor getExtractor(ExtractConfig.ExtractAuto type) {
    return autoExtractors.get(type.toString());
  }
}
