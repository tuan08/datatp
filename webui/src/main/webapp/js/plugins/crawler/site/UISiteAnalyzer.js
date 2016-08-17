define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'plugins/crawler/site/UIURLStructure',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UIURLStructure, Rest) {

  var UISiteAnalyzer = UICollabsible.extend({
    label: "Site Analyzer", 
    config: {
      actions: [
        {
          action: "analyze", label: "Analyze",
          onClick: function(thisUI) { thisUI.analyze(); }
        },
        {
          action: "refresh", label: "Refresh",
          onClick: function(thisUI) { thisUI.refresh(); }
        }
      ]
    },

    onInit: function(options) {
      this.siteConfig = options.siteConfig;
      var siteStructure = Rest.site.getAnalyzedSiteStructure(this.siteConfig, 100, false);
      this.add(new UIURLStructure({siteStructure: siteStructure}));
    },

    refresh: function(siteStructure) {
      var siteStructure = Rest.site.getAnalyzedSiteStructure(this.siteConfig, 100, false);
      this.clear();
      this.add(new UIURLStructure({siteStructure: siteStructure}));
      this.render();
    },

    analyze: function(siteStructure) {
      var siteStructure = Rest.site.getAnalyzedSiteStructure(this.siteConfig, 100, true);
      this.clear();
      this.add(new UIURLStructure({siteStructure: siteStructure}));
      this.render();
    },
    
  }) ;

  return UISiteAnalyzer ;
});
