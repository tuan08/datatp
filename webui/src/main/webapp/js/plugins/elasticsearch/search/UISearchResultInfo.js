define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UITable',
  'ui/UICollapsible',
], function($, _, Backbone, UIContent, UITable, UICollapsible) {
  var UIExecuteHistory = UITable.extend({
    label: "Query Execute Histories",

    config: {
      toolbar: {
        dflt: {
          actions: [ ]
        }
      },
      
      bean: {
        label: "Query Execute Histories",
        fields: [
          { 
            field: "from",   label: "From", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.query.from; } }
          },
          { 
            field: "size",     label: "Size", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.query.size; } }
          },
          { 
            field: "shardTotal",     label: "Shard Total", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.shards.total; } }
          },
          { 
            field: "shardSuccessful",     label: "Shard Successful", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.shards.successful; } }
          },
          { 
            field: "shardFailed",     label: "Shard Failed", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.shards.failed; } }
          },
          { 
            field: "hitTotal",     label: "Hit Total", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.hitTotal; } }
          },
          { 
            field: "hitReturn",     label: "Hit Return", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.hitReturn; } }
          },
          { 
            field: "took",     label: "Took", toggled: true, filterable: true,
            custom: { getDisplay: function(bean) { return bean.resultInfo.took; } }
          },
        ]
      }
    },
    onInit: function(options) {
      this.setBeans([]);
    },

    onResult: function(queryResult) {
      this.queryResult = queryResult;
      this.setBeans(queryResult.queryHistories);
    }
  });

  var UISearchResultInfo = UICollapsible.extend({
    label: "Search Result Info", 
    config: {
      actions: [ ]
    },
    
    onInit: function(options) {
      this.uiQuery = new UIContent({highlightSyntax: "json", content: ""}) ;
      this.uiQuery.label = "Query";
      this.add(this.uiQuery) ;

      this.uiExecuteHistory = new UIExecuteHistory() ;
      this.add(this.uiExecuteHistory) ;
    },

    onResult: function(queryResult) {
      this.uiExecuteHistory.onResult(queryResult);
      if(queryResult.queryHistories.length > 0) {
        this.uiQuery.setContent(JSON.stringify(queryResult.queryHistories[0].query.query, null, 2));
      }
    }
  }) ;
  
  return UISearchResultInfo ;
});
