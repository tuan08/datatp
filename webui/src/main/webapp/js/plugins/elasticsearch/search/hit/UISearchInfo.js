define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/table/UITable',
  'ui/UICollapsible',
], function($, _, Backbone, UIContent, UITable, UICollapsible) {

  var ExecuteHistory = {
    label: 'Query Execute Info',
    fields: {
      "query.from": { label: "From", datatype: "integer" },
      "query.size": { label: "Size", datatype: "integer" },
      "resultInfo.shards.total": { label: "Number Of Shards", datatype: "integer" },
      "resultInfo.shards.successful": { label: "Shard Successful", datatype: "integer" },
      "resultInfo.shards.failed": { label: "Shard Failed", datatype: "integer" },
      "resultInfo.hitTotal": { label: "Hit Total", datatype: "integer" },
      "resultInfo.hitReturn": { label: "Hit Return", datatype: "integer" },
      "resultInfo.took": { label: "Took", datatype: "integer" }
    }
  };

  var UIExecuteHistory = UITable.extend({
    label: "Query Execute Histories",

    config: {
      control: { header: "Table Control Demo"},
      table: { header: "Demo Table"},
    },

    withQueryResult: function(queryResult) {
      this.set(ExecuteHistory, queryResult.queryHistories);
    }
  });

  var UISearchInfo = UICollapsible.extend({
    label: "Search Result Info", 
    config: { },
    
    onInit: function(options) {
      this.uiQuery = new UIContent({highlightSyntax: "json", content: ""}) ;
      this.uiQuery.label = "Query";
      this.add(this.uiQuery) ;

      this.uiExecuteHistory = new UIExecuteHistory() ;
      this.add(this.uiExecuteHistory) ;
    },

    withQueryResult: function(queryResult) {
      this.uiExecuteHistory.withQueryResult(queryResult);
      if(queryResult.queryHistories.length > 0) {
        this.uiQuery.setContent(JSON.stringify(queryResult.queryHistories[0].query.query, null, 2));
      }
      return this;
    }
  }) ;
  
  return UISearchInfo ;
});
