package net.datatp.crawler.site.analysis;

import java.io.IOException;
import java.util.ArrayList;

public class URLSiteStructure extends ArrayList<URLAnalysis>{
  private static final long serialVersionUID = 1L;

  public void dump(Appendable out) throws IOException {
    for(URLAnalysis sel : this) {
      out.append(sel.toString()).append("\n");
    }
  }
}