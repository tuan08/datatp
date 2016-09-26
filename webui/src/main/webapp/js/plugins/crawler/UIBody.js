define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UINavigation'
], function($, _, Backbone, UIContent, UINavigation) {
  var UIBody = UINavigation.extend({
    onInit: function(options) {
      var onClick = function(thisNav, menu, item) {
        require(['plugins/crawler/' + item.config.module], function(UIDemoComponent) { 
          thisNav.setWorkspace(UIDemoComponent);
        }) ;
      };
      var crawlerMenu = this.addMenu("crawler", "Crawler", { collapse: false });
      crawlerMenu.addItem("Status", { module: "UICrawlerStatus" }, onClick);

      var siteMenu = this.addMenu("site", "Site", { collapse: false });
      siteMenu.addItem("Config", { module: "site/UISiteConfigScreen" }, onClick);
      siteMenu.addItem("Statistics", { module: "site/UISiteStatistics" }, onClick);

      var thisNav = this;
      require(['plugins/crawler/UICrawlerStatus'], function(UIDemoComponent) { 
        thisNav.setWorkspace(UIDemoComponent);
      }) ;
    }
  });

  return new UIBody() ;
});
