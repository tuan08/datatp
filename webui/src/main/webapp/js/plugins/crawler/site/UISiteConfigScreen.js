define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/site/UISiteConfigList',
  'plugins/crawler/site/UISiteConfig'
], function($, _, Backbone, UITabbedPane, UISiteConfigList, UISiteConfig) {
  var UISiteConfigScreen = UITabbedPane.extend({
    type: 'UISiteConfigScreen',
    label: 'Site Configs',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      this.addTab("siteConfigList", "Site Config", new UISiteConfigList(), false, true);
    },
    
    addSiteConfigTab: function(siteConfig) {
      var name = siteConfig.hostname;
      this.addTab(name, name, new UISiteConfig( {siteConfig: siteConfig} ), true, true);
      this.render();
    }
  });

  return new UISiteConfigScreen() ;
});
