define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UIContent',
  'plugins/elasticsearch/search/UISearch',
], function($, _, Backbone, UITabbedPane, UIContent, UISearch) {
  var UIBody = UITabbedPane.extend({
    label: 'Search Screen',

    config: {
      tabs: [
        { 
          label: "Search",  name: "search",
          onSelect: function(thisUI, tabConfig) {
            var uiTab1 = new UIContent( { content: "Search" }) ;
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UISearch()) ;
          }
        },
        { 
          label: "ES",  name: "es",
          onSelect: function(thisUI, tabConfig) {
            var uiTab2 = new UIContent( { content: "Elasticsearch Admin" }) ;
            thisUI.setSelectedTabUIComponent(tabConfig.name, uiTab2) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
    }
  });
  return new UIBody({}) ;
});
