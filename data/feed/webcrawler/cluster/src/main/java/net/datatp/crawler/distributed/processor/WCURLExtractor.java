package net.datatp.crawler.distributed.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.urldb.URLDatumFactory;

@Component
public class WCURLExtractor extends URLExtractor {
  
  @Value("#{'${crawler.processor.urlextractor.exclude-pattern}'.split(',')}")
  public void setExcludePatterns(List<String> list) { super.setExcludePatterns(list); }

  @Autowired
  public void setURLDatumFactory(URLDatumFactory factory) {
    this.urlDatumFactory = factory;
  }
}
