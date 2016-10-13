define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/table/UITable',
  'plugins/elasticsearch/search/hit/UISearchHitDetail',
  'plugins/elasticsearch/search/hit/UISearchInfo'
], function($, _, Backbone, UITable, UISearchHitDetail, UISearchInfo) {

  var UISearchHit = UITable.extend({
    label: "Search Hit", 

    config: {
      control: { header: "Search Hit"},
      table: { 
        header: "Search Hit Result",
        page: { size: 25 }
      },
      actions: {
        toolbar: {
          queryInfo: {
            label: "Search Info",
            onClick: function(uiTable) { 
              var name = "Search Info" ;
              var uiInfo = new UISearchInfo().withQueryResult(uiTable.queryResult);
              uiTable.addWorkspaceTabPluginUI(name, name, uiInfo, true, true);
              uiTable.refreshWorkspace();
            }
          }
        },
        bean: {
          detail: {
            label: "Detail",
            onClick: function(uiTable, beanState) {
              var bean = beanState.bean;
              var name = bean._id ;
              var uiDetail = new UISearchHitDetail().withHit(bean);
              uiTable.addWorkspaceTabPluginUI(name, name, uiDetail, true, true);
              uiTable.refreshWorkspace();
            }
          }
        }
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
      this.queryResult = esQueryCtx.getQueryResult();
      this.setBeans(this.queryResult.hits, true);
    }
  }) ;

  return UISearchHit ;
});
