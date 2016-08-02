package net.datatp.xhtml.extract;

import java.util.ArrayList;
import java.util.List;

public class WDataExtractors {
  private String           name;
  private WDataExtractor[] extractor;
  
  public WDataExtractors(String name, WDataExtractor ... extractor) {
    this.extractor = extractor;
  }
  
  public String getName() { return this.name; }
  
  public List<WDataExtract> extract(WDataExtractContext context) {
    List<WDataExtract> holder = new ArrayList<>();
    for(int i = 0; i < extractor.length; i++) {
      WDataExtract extract = extractor[i].extract(context);
      if(extract != null) holder.add(extract);
    }
    if(holder.size() == 0) return null;
    return holder;
  }
}
