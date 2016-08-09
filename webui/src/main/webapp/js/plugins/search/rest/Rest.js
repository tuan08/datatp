define([
  'util/PageIteratorModel',
  'rest/Server'
], function(PageIteratorModel, Server) {

  var server = new Server("http://localhost:8080/search");
  
  function XDocQuery(query, pageSize) {
    this.query = query;
    this.pageSize  = pageSize == null ?  10 : pageSize;
    this.searchResult = null;
 
    this.execute = function() {
      var params = {query: this.query, from: 0, pageSize: this.pageSize};
      this.searchResult = server.restGET("/query", params);
      this.pageItrModel = new PageIteratorModel(1, this.pageSize, this.searchResult.hits.total);
    };

    this.goToPage = function(page) {
      this.pageItrModel.checkAndSetPage(page);
      var params = {query: this.query, from: this.pageItrModel.getFrom(), pageSize: this.pageSize};
      this.searchResult = server.restGET("/query", params);
    };
    
    this.getExecuteTime = function() { return this.searchResult == null ? 0 : this.searchResult.took; };
    
    this.hasSearchResult = function() { return this.searchResult != null; }

    this.getShardInfo = function() { 
      return this.searchResult == null ? 0 : this.searchResult._shards;
    };

    this.getHitTotal = function() { 
      return this.searchResult == null ? 0 : this.searchResult.hits.total;
    };

    this.getHits = function() { 
      if(this.searchResult == null) return [];
      return this.searchResult.hits.hits;
    };
     
    this.getPageIteratorModel = function() { return this.pageItrModel; };

    this.dump = function() {
      console.log("Query: " + this.query);
      console.log("Range: " + this.from + " - " + this.to);
      if(!this.hasSearchResult()) return ;
      console.log("Search Time: " + this.getExecuteTime());
      var shardInfo = this.getShardInfo();
      console.log("Shards: total " + shardInfo.total + ", successful = " + shardInfo.successful) ;
      console.log("Hits: total " + this.getHitTotal()) ;
      var hits = this.getHits();
      for(var i = 0; i < hits.length; i++) {
        var hit = hits[i];
        var source = hit._source;
        var title = "No Entity found";
        if(source.entity != null && source.entity.content != null) {
          title = source.entity.content.title
        }
        console.log("  " + title) ;
      }
    };
  };

  function XDocSearcher() {
    this.createXDocQuery = function(query, pageSize) {
      var xdocQuery = new XDocQuery(query, pageSize);
      return xdocQuery;
    };
  };

  var Rest = {
    searcher: new XDocSearcher(),
  }
  return Rest ;
});
