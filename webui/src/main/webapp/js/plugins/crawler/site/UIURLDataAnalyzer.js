define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'plugins/crawler/site/UIURLAnalyzer',
  'plugins/crawler/site/UIXhtmlAnalyzer',
  'plugins/crawler/Rest'
], function($, _, Backbone, UITabbedPane, UIURLAnalyzer, UIXhtmlAnalyzer, Rest) {
  var UIURLDataAnalyzer = UITabbedPane.extend({
    label: 'URL Structure Analyzer',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      var urlAnalysis = options.urlAnalysis;
      var urlInfo = urlAnalysis.urlInfo;
      console.printJSON(urlInfo);
      var urlData =  Rest.site.getAnalyzedURLData(urlInfo.url);
      var opts = {
        urlInfo:      urlInfo,
        siteConfig:   options.siteConfig,
        urlData:      urlData
      }
      this.addTab("URLAnalyzer", "URL Analyzer", new UIURLAnalyzer(opts), false, true);
      this.addTab("XhtmlAnalyzer", "Xhtml Analyzer", new UIXhtmlAnalyzer(opts), false, false);
    },
  });

  return UIURLDataAnalyzer ;
});
