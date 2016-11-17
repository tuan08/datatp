package net.datatp.es.sysinfo;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.es.NodeBuilder;
import net.datatp.es.sys.SysInfoLoggerService;
import net.datatp.model.sys.Sys;
import net.datatp.sys.RuntimeEnv;

@RunWith(RandomizedRunner.class)
@ThreadLeakScope(Scope.SUITE)
@ThreadLeakLingering(linger = 3000)
public class SysInfoLoggerServiceUnitTest {
  static String WORKING_DIR = "build/working";
  
  protected final Logger logger = Loggers.getLogger(getClass());
  
  private Node node ;
  
  @Before
  public void setup() throws Exception {
    node = new NodeBuilder().newNode();
  }
  
  @After
  public void after() throws IOException {
    node.close();
  }
  
  @Test
  public void testService() throws Exception {
    SysInfoLoggerService service1 = new SysInfoLoggerService();
    service1.onInit(new RuntimeEnv("localhost", "vm-1", WORKING_DIR));
    service1.setLogPeriod(3000);
    
    SysInfoLoggerService service2 = new SysInfoLoggerService();
    service2.onInit(new RuntimeEnv("localhost", "vm-2", WORKING_DIR));
    service2.setLogPeriod(3000);
    
    Thread.sleep(15000);
    service1.onDestroy();
    service2.onDestroy();
    
    ESClient esclient = new ESClient(new String[] { "127.0.0.1:9300" });
    ESObjectClient<Sys> esObjecClient = new ESObjectClient<Sys>(esclient, "sys-info", Sys.class) ;
    logger.info("SysMetric records = " + esObjecClient.count(termQuery("host", "vm-1")));
    logger.info("SysMetric Heap Memory = " + esObjecClient.count(termQuery("metric.mem.name", "Heap")));
    
    AbstractAggregationBuilder[] aggregationBuilders = { 
      AggregationBuilders.terms("by_vmName").field("host"),
      AggregationBuilders.count("count_by_vmName").field("host"),
      AggregationBuilders.
        dateRange("by_timestamp").field("timestamp").format("dd/MM/yyyy HH:mm:ss").
        addRange("8:00PM  - 10:00PM", "04/04/2016 10:00:00", "04/04/2016 22:00:00")
    };
    SearchResponse searchResponse = esObjecClient.search(null, aggregationBuilders, true, 0, 3);
    SearchHits hits = searchResponse.getHits();
    logger.info("Total  Hits = " + hits.getTotalHits());
    logger.info("Return Hits = " + hits.getHits().length);
    Aggregations aggregations = searchResponse.getAggregations() ;
    dump(aggregations, "");
    
    logger.info(esObjecClient.getQueryExecutor().matchAll().setAggregations(aggregationBuilders).executeAndReturnAsJson());
    esclient.close();
  }
  
  private void dump(Aggregations aggregations, String indent) {
    for(Aggregation sel : aggregations.asList()) {
      System.out.println(indent + "aggregation = " + sel.getName() + ", type = " + sel.getClass());
      if(sel instanceof StringTerms) {
        StringTerms stringTerms = (StringTerms) sel;
        List<Terms.Bucket> buckets = stringTerms.getBuckets() ;
        for(Terms.Bucket selBucket : buckets) {
          System.out.println(indent + "  key = " + selBucket.getKey()) ;
          System.out.println(indent + "     doc count = " + selBucket.getDocCount());
        }
      } else if(sel instanceof InternalValueCount) {
        InternalValueCount count = (InternalValueCount) sel;
        System.out.println(indent + "  count = " + count.getValue()) ;
      } else if(sel instanceof InternalDateRange) {
        InternalDateRange dateRange = (InternalDateRange)sel;
        System.out.println(indent + "  dateRange = " + dateRange.getName()) ;
        Collection<InternalDateRange.Bucket> buckets = dateRange.getBuckets();
        for(InternalDateRange.Bucket bucket : buckets) {
          System.out.println(indent + "    key = " + bucket.getKey()) ;
          System.out.println(indent + "    from = " + bucket.getFromAsString()) ;
          System.out.println(indent + "    to = " + bucket.getToAsString()) ;
          System.out.println(indent + "    doc count = " + bucket.getDocCount()) ;
        }
      }
    }
  }
}
