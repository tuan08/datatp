package net.datatp.nlp.query;

import org.junit.Test;

import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;
import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.DateTokenAnalyzer;
import net.datatp.nlp.token.analyzer.EmailTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TimeTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.USDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNMobileTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNPhoneTokenAnalyzer;
import net.datatp.util.dataformat.DataSerializer;
import net.datatp.util.io.IOUtil;

public class YamlFormatUnitTest {
  
  @Test
  public void test() throws Exception {
    SynsetDictionary dict = new SynsetDictionary() ;
    dict.add("lienhe", new String[]{}, new String[]{ "liên hệ" }) ;
    EntityDictionary entityDict = new EntityDictionary(EntityDictionary.DICT_RES) ;
    MatcherResourceFactory umFactory = new MatcherResourceFactory(dict, entityDict) ;

    Query query=  getQuery();
    query.compile(umFactory) ;

    QueryDocument document = createDocument(
      "title: this is a document title",
      "body:  liên hệ 186 trương định quận hai bà trưng hà nội việt nam"
    ) ;
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }

  protected QueryDocument createDocument(String ... fields) throws Exception {
    QueryDocument document = new QueryDocument() ;
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new DateTokenAnalyzer(), new TimeTokenAnalyzer(), 
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(),
        new VNPhoneTokenAnalyzer(), new VNMobileTokenAnalyzer(),
        new EmailTokenAnalyzer()  
    };
    for(String selField : fields) {
      int idx      = selField.indexOf(':') ;
      String fname = selField.substring(0, idx).trim() ;
      String data  = selField.substring(idx + 1).trim() ;
      document.add(fname, data, analyzer) ;
    }
    return document ;
  }

  
  public Query getQuery() throws Exception {
    String yamlData = IOUtil.getFileContentAsString("src/test/resources/query.yaml");
    QueryProject project = DataSerializer.YAML.fromString(yamlData, QueryProject.class);
    System.out.println(DataSerializer.JSON.toString(project));
    return project.getQueries().get("test");
  }
}
