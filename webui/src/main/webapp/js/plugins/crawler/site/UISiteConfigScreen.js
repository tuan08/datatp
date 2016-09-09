define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/site/UISiteConfigs',
  'plugins/crawler/site/UISiteConfig'
], function($, _, Backbone, UITabbedPane, UISiteConfigs, UISiteConfig) {
  var UISiteConfigScreen = UITabbedPane.extend({
    type: 'UISiteConfigScreen',
    label: 'Site Configs',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      this.addTab("siteConfigList", "Site Configs", new UISiteConfigs(), false, true);
    },
    
    addSiteConfigTab: function(siteConfig) {
      var name = siteConfig.hostname;
      this.addTab(name, name, new UISiteConfig( {siteConfig: siteConfig} ), true, true);
      this.render();
    }
  });

  return new UISiteConfigScreen() ;
});
