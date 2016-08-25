define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/site/UIURLAnalyzer',
  'plugins/crawler/site/UIXhtmlAnalyzer',
  'plugins/crawler/Rest'
], function($, _, Backbone, UITabbedPane, UIURLAnalyzer, UIXhtmlAnalyzer, Rest) {
  var UIURLStructureAnalyzer = UITabbedPane.extend({
    label: 'URL Structure Analyzer',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      var urlInfo = options.urlInfo
      var urlStructure =  Rest.site.getAnalyzedURLStructure(urlInfo.url);
      var opts = {
        urlInfo:      urlStructure.urlAnalyzer,
        siteConfig:   options.siteConfig,
        urlStructure: urlStructure
      }
      this.addTab("URLAnalyzer", "URL Analyzer", new UIURLAnalyzer(opts), false, true);
      this.addTab("XhtmlAnalyzer", "Xhtml Analyzer", new UIXhtmlAnalyzer(opts), false, false);
    },
  });

  return UIURLStructureAnalyzer ;
});
