package net.datatp.nlp.query;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.USDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;

public class OpinionQueryUnitTest {
  final static public String OPINION_QUERY = 
      "{" +
      "  \"name\" : \"test query\" ," +
      "  \"priority\":  1 ," +
      "  \"description\": \"extract the sentence opinion\"," +
      "  \"matchmax\" : 3 ," +
      "  \"matchselector\" : \"first\" ," +
      "  \"prematch\": [" +
      "    \"print: call prematch for query test query\"," +
      "    \"if-match: / synset{name=opinion}  ? $print{match keyword opinion} : $exit \"" +
      "  ]," +
      "  \"match\": [" +
      "    \"/p synset{name=opinion}\"" +
      "  ]," +
      "  \"extract\": [" +
      "  ]," +
      "  \"postmatch\": [" +
      "    \"print: call postmatch for query test query\", " +
      "    \"if: $extractCount{lienhe:*} > 3 ? $print{tag count > 3} : $print{tag count < 3}\"" +
      "  ]"  +
      "}" ;

  @Test
  public void test() throws Exception {
    SynsetDictionary dict = new SynsetDictionary(SynsetDictionary.DICT_RES) ;
    EntityDictionary entityDict = new EntityDictionary(EntityDictionary.DICT_RES) ;
    MatcherResourceFactory resFactory = new MatcherResourceFactory(dict, entityDict) ;

    Query query=  create(OPINION_QUERY);
    query.compile(resFactory) ;

    QueryDocument document = createDocument(
        "title: this is a document title",
        "body:  chiếc iphone này đẹp"
        ) ;
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }

  protected QueryDocument createDocument(String ... fields) throws Exception {
    QueryDocument document = new QueryDocument() ;
    TokenAnalyzer[] analyzer = {
      PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(),
      new VNDTokenAnalyzer(), new USDTokenAnalyzer()
    };
    for(String selField : fields) {
      int idx      = selField.indexOf(':') ;
      String fname = selField.substring(0, idx).trim() ;
      String data  = selField.substring(idx + 1).trim() ;
      document.add(fname, data, analyzer) ;
    }
    return document ;
  }

  static public Query create(String json) throws JsonParseException, IOException {
    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    return mapper.readValue(json , Query.class);
  }
}
