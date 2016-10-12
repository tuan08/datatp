define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/table/UITable'
], function($, _, Backbone, UITable) {

  var UISearchHit = UITable.extend({
    label: "Search Hit", 

    config: {
      control: { header: "Search Hit"},
      table: { 
        header: "Search Hit Result",
        page: { size: 25 }
      },
      actions: {
      }
    },
    
    onInit: function(options) {
      var esQueryCtx = options.esQueryContext ;

      this.addDefaultControlPluginUI();
      this.set(esQueryCtx.searchHitModel, []);
      this.setTableColumnsVisible(this.getTableColumns(), false, false);
      this.setTableColumnsVisible(["_id"], true, false);
    },

    onSearch: function(esQueryCtx) {
      var queryResult = esQueryCtx.getQueryResult();
      this.setBeans(queryResult.hits, true);
    }
  }) ;

  return UISearchHit ;
});
