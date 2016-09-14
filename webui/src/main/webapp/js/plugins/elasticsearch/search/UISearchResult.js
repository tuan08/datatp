define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/elasticsearch/search/UISearchResultInfo',
  'plugins/elasticsearch/search/hit/UISearchHit'
], function($, _, Backbone, UITabbedPane, UISearchResultInfo, UISearchHit) {
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
          label: "Chart",  name: "chart",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiSearchResultInfo) ;
          }
        },
        { 
          label: "Info",  name: "info",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiSearchResultInfo) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      this.uiSearchHit = new UISearchHit();
      this.uiSearchResultInfo = new UISearchResultInfo();
    },


    onResult: function(result) {
      this.uiSearchHit.onResult(result);
      this.uiSearchResultInfo.onResult(result);
    }
  });
  return UISearchResult ;
});
