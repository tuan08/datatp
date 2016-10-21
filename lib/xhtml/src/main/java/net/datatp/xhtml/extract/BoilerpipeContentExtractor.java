package net.datatp.xhtml.extract;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.ExtractorBase;
import de.l3s.boilerpipe.filters.english.IgnoreBlocksAfterContentFilter;
import de.l3s.boilerpipe.filters.english.NumWordsRulesClassifier;
import de.l3s.boilerpipe.filters.english.TerminatingBlocksFinder;
import de.l3s.boilerpipe.filters.heuristics.BlockProximityFusion;
import de.l3s.boilerpipe.filters.heuristics.DocumentTitleMatchClassifier;
import de.l3s.boilerpipe.filters.heuristics.ExpandTitleToContentFilter;
import de.l3s.boilerpipe.filters.heuristics.KeepLargestBlockFilter;
import de.l3s.boilerpipe.filters.heuristics.LargeBlockSameTagLevelToContentFilter;
import de.l3s.boilerpipe.filters.heuristics.ListAtEndFilter;
import de.l3s.boilerpipe.filters.heuristics.TrailingHeadlineToBoilerplateFilter;
import de.l3s.boilerpipe.filters.simple.BoilerplateBlockFilter;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import net.datatp.util.ExceptionUtil;
import net.datatp.util.text.CosineSimilarity;

public class BoilerpipeContentExtractor implements WDataExtractor {
  private String type =  "content"; 
  
  public BoilerpipeContentExtractor(String type) { 
    this.type = type; 
  }
  
  @Override
  public WDataExtract extract(WDataContext context) {
    throw new RuntimeException("Not supported");
  }
  
  public ExtractEntity extractEntity(WDataContext context) {
    try {
      return boilerpipeExtract(context);
    } catch (Exception e) {
      ExtractEntity entity = new ExtractEntity("content", type);
      entity.withContent(ExceptionUtil.getStackTrace(e));
      entity.addTag(new String[] {"extractor:boilerpipe", "extractor:error"});
      return entity;
    }
  }
  
  ExtractEntity boilerpipeExtract(WDataContext context) throws Exception {
    String anchorText = context.getWdata().getAnchorText();
    String xhtml = context.createDocument().html();
    BoilerpipeSAXInput in = new BoilerpipeSAXInput(new InputSource(new StringReader(xhtml)));
    TextDocument doc = in.getTextDocument();
    ContentExtractor.INSTANCE.process(doc);
    List<TextBlock> textBlocks = doc.getTextBlocks();
    boolean pageList = isPageList(anchorText, textBlocks);
    List<String> contentHolder = new ArrayList<>();
    for(int i = 0; i < textBlocks.size(); i++) {
      TextBlock block = textBlocks.get(i);
      if(!block.isContent()) continue;
      contentHolder.add(block.getText());
    }
    
    ExtractEntity entity = new ExtractEntity("content", type);
    if(contentHolder.size() > 0) {
      String firstP = contentHolder.get(0);
      String[] lines = firstP.split("\n");
      String title       = anchorText;
      StringBuilder description = new StringBuilder();
      for(int i = 0; i < lines.length; i++) {
        String line = lines[i].trim();
        if(CosineSimilarity.INSTANCE.similarity(anchorText, line) > 0.8) {
          title = line;
          for(int j = i + 1; j < lines.length; j++) {
            description.append(lines[j]);
            if(description.length() > 50);
          }
          break;
        }
      }
      if(description.length() < 50 && contentHolder.size() > 1) {
        String secondP = contentHolder.get(1);
        for(String line : secondP.split("\n")) {
          line = line.trim();
          description.append(line).append("\n");
          if(description.length() > 50) break;
        }
      }
      if(title == null) title = context.getWdata().getUrl();
      if(description.length() > 200) description.setLength(200);
      
      entity.withTitle(title);
      entity.withDescription(description.toString());
      entity.withContent(contentHolder.toArray(new String[contentHolder.size()]));
    } else {
      if(anchorText == null || anchorText.length() == 0) anchorText = context.getWdata().getUrl();
      entity.withTitle(anchorText);
      entity.withContent(doc.getText(true, true));
    }
    String webpageType = "webpage:detail";
    if(pageList) webpageType = "webpage:list";
    
    entity.addTag(new String[] {"extractor:boilerpipe", webpageType});
    return entity;
  }
  
  
  boolean isPageList(String anchorText, List<TextBlock> textBlocks) {
    boolean pageList = false;
    if(anchorText == null || anchorText.length() < 20) {
      float sumLinkDensity = 0;
      int   blockContentCount = 0;
      for(int i = 0; i < textBlocks.size(); i++) {
        TextBlock block = textBlocks.get(i);
        if(block.isContent()) {
          blockContentCount++;
          sumLinkDensity += block.getLinkDensity();
        }
      }
      if(blockContentCount > 0 && sumLinkDensity/blockContentCount > 0.1f) pageList = true;
    }
    return pageList;
  }
  
  /** A modified version of  {@link ArticleExtractor}. */
  static public class ContentExtractor extends ExtractorBase {
    public static final ContentExtractor INSTANCE = new ContentExtractor();

    public static ContentExtractor getInstance() { return INSTANCE; }
    
    public boolean process(TextDocument doc) throws BoilerpipeProcessingException {
      boolean processResult = 
          TerminatingBlocksFinder.INSTANCE.process(doc)
          | new DocumentTitleMatchClassifier(doc.getTitle()).process(doc)
          | NumWordsRulesClassifier.INSTANCE.process(doc)
          | IgnoreBlocksAfterContentFilter.DEFAULT_INSTANCE.process(doc)
          | TrailingHeadlineToBoilerplateFilter.INSTANCE.process(doc)
          | BlockProximityFusion.MAX_DISTANCE_1.process(doc)
          | BoilerplateBlockFilter.INSTANCE_KEEP_TITLE.process(doc)
          | BlockProximityFusion.MAX_DISTANCE_1_CONTENT_ONLY_SAME_TAGLEVEL.process(doc)
          | KeepLargestBlockFilter.INSTANCE_EXPAND_TO_SAME_TAGLEVEL_MIN_WORDS.process(doc)
          | ExpandTitleToContentFilter.INSTANCE.process(doc)
          | LargeBlockSameTagLevelToContentFilter.INSTANCE.process(doc)
          | ListAtEndFilter.INSTANCE.process(doc) ;
      return processResult;
    }
  }
}
