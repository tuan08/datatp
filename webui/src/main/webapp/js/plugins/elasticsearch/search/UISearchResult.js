define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/elasticsearch/search/hit/UISearchHit',
  'plugins/elasticsearch/search/analytic/UIAnalytics'
], function($, _, Backbone, UITabbedPane, UISearchHit, UIAnalytics) {

  var UISearchResult = UITabbedPane.extend({
    label: 'Search Result Workspace',

    config: {
      tabs: [
        { 
          label: "Search Hits",  name: "searchHit",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiSearchHit) ;
          }
        },
        { 
          label: "Analytics",  name: "analytics",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiAnalytics) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      var esQueryContext = options.esQueryContext;
      this.uiSearchHit = new UISearchHit();
      this.uiSearchHit.onResult(esQueryContext.getQueryResult());
      
      this.uiAnalytics = new UIAnalytics({esQueryContext: esQueryContext});
    },


    onSearch: function(esQueryCtx) {
      var result = esQueryCtx.getQueryResult();
      this.uiSearchHit.onResult(result);
      this.uiAnalytics.onSearch(esQueryCtx);
    }
  });
  return UISearchResult ;
});
