define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UIContent',
  'plugins/elasticsearch/search/hit/UISearchHit'
], function($, _, Backbone, UITabbedPane, UIContent, UISearchHit) {
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
            var uiTab2 = new UIContent( { content: "Chart" }) ;
            thisUI.setSelectedTabUIComponent(tabConfig.name, uiTab2) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      this.uiSearchHit = new UISearchHit();
    },


    onResult: function(result) {
      this.uiSearchHit.onResult(result);
    }
  });
  return UISearchResult ;
});
