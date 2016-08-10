package net.datatp.crawler.rest;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.datatp.es.ESQueryExecutor;
import net.datatp.search.ESXDocSearcher;

@RestController
@CrossOrigin(origins = "*")
public class XDocSearcherController {
  
  private ESXDocSearcher searcher ;
  
  @PostConstruct
  public void onInit() throws Exception {
    searcher = new ESXDocSearcher("xdoc", new String[] {"127.0.0.1:9300"});
  }
  
  @RequestMapping("/search/query")
  public String search(@RequestParam(value="query") String query, 
                       @RequestParam(value="from", defaultValue="0") int from, 
                       @RequestParam(value="pageSize",   defaultValue="100") int pageSize) throws Exception {
    ESQueryExecutor executor = searcher.getContentQueryExecutor(query);
    executor.setFrom(from).setPageSize(pageSize);
    return executor.executeAndReturnAsJson();
  }  
}