define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/UICrawler',
  'plugins/crawler/site/UISiteConfigScreen',
  'plugins/crawler/site/UISiteStatistics',
], function($, _, Backbone, UITabbedPane, UICrawler, UISiteConfigScreen, UISiteStatistics) {
  var UIBody = UITabbedPane.extend({
    label: 'Crawler',

    config: {
      style: "ui-round-tabs",
      tabs: [
        { 
          label: "Crawler",  name: "crawler",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UICrawler()) ;
          }
        },
        { 
          label: "Site",  name: "site",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UISiteConfigScreen()) ;
          }
        },
        { 
          label: "Report",  name: "report",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UISiteStatistics()) ;
          }
        }
      ]
    }
  });

  return new UIBody() ;
});
