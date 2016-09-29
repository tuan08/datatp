define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/site/UISiteConfigList',
  'plugins/crawler/site/UISiteConfigDetail'
], function($, _, Backbone, UITabbedPane, UISiteConfigList, UISiteConfigDetail) {
  var UISiteConfigScreen = UITabbedPane.extend({
    type: 'UISiteConfigScreen',
    label: 'Site Configs',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      this.addTab("siteConfigList", "Site Configs", new UISiteConfigList(), false, true);
    },
    
    addSiteConfigTab: function(siteConfig) {
      var name = siteConfig.hostname;
      this.addTab(name, name, new UISiteConfigDetail( {siteConfig: siteConfig} ), true, true);
      this.render();
    }
  });

  return UISiteConfigScreen ;
});
