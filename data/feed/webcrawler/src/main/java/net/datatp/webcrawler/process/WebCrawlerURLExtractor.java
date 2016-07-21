package net.datatp.webcrawler.process;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.URLExtractor;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.webcrawler.urldb.URLDatumRecord;

@Component
public class WebCrawlerURLExtractor extends URLExtractor {
  @Value("#{'${crawler.processor.urlextractor.exclude-pattern}'.split(',')}")
  public void setExcludePatterns(List<String> list) {
    super.setExcludePatterns(list);
  }
  
  @PostConstruct
  public void onInit() {
    configure();
  }
  
  protected URLDatum createURLDatumInstance(long timestamp) {
    return new URLDatumRecord(timestamp);
  }
}
