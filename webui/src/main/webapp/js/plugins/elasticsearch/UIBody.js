define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/elasticsearch/search/UISearch',
  'plugins/elasticsearch/admin/UIAdmin'
], function($, _, Backbone, UITabbedPane, UISearch, UIAdmin) {
  var UIBody = UITabbedPane.extend({
    label: 'Search Screen',

    config: {
      tabs: [
        { 
          label: "Search",  name: "search",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, thisUI.uiSearch) ;
          }
        },
        { 
          label: "Admin",  name: "admin",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UIAdmin()) ;
          }
        }
      ]
    },
    
    onInit: function(options) {
      this.uiSearch = new UISearch();
    }
  });
  return new UIBody({}) ;
});
