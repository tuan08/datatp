package net.datatp.xhtml.xpath;

public interface XPathRepetionAnalyzer {
  final static XPathRepetionAnalyzer LINK_ANALYZER = new LinkRepetionAnalyzer();
  final static XPathRepetionAnalyzer TEXT_ANALYZER = new TextRepetionAnalyzer();
  
  public void analyze(XPathStructure structure, XPathRepetion repetion);

  static public class LinkRepetionAnalyzer implements XPathRepetionAnalyzer {
    @Override
    public void analyze(XPathStructure structure, XPathRepetion repetion) {
      XPathRepetion.Info info = repetion.getInfo();
      if(info.getMaxDepth() > 10) return;
      double linkPresentRatio = info.getLinkPresentRatio();
      if(linkPresentRatio > 0.8) {
        if(info.getAvgTextLength() > 30) {
          repetion.attr("tag:repetion", "link:related");
        } else {
          repetion.attr("tag:repetion", "link");
        }
      }
    }
  }
  
  static public class TextRepetionAnalyzer implements XPathRepetionAnalyzer {
    @Override
    public void analyze(XPathStructure structure, XPathRepetion repetion) {
      XPathRepetion.Info info = repetion.getInfo();
      if(info.getMaxDepth() > 10) return;
      if(info.getAvgTextLength() > 150 && info.getAvgLinkTextDensity() < 0.25) {
        if(info.getAvgTextLength() > 450) {
          repetion.attr("tag:text", "text:big");
        } else if(info.getAvgTextLength() > 300) {
          repetion.attr("tag:text", "text:medium");
        } else {
          repetion.attr("tag:text", "text:small");
        }
      }
    }
  }
}