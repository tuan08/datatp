define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UIBorderLayout',
  'ui/UIProperties',
  'ui/UITabbedPane',
  'plugins/crawler/site/UIURLAnalyzer',
  'plugins/crawler/site/UIExtractConfig',
  'plugins/crawler/site/UIXhtmlIFrame',
  'plugins/crawler/Rest'
], function($, _, Backbone, UIContent, UIBorderLayout, UIProperties, UITabbedPane, UIURLAnalyzer, UIExtractConfig, UIXhtmlIFrame, Rest) {
  var UIWebPageAnalyzer = UIBorderLayout.extend({
    label: 'WebPage Analyzer',

    config: {
    },
    
    onInit: function(options) {
      var urlAnalysis = options.urlAnalysis;
      var urlInfo = urlAnalysis.urlInfo;
      var urlData =  Rest.site.getAnalyzedURLData(urlInfo.url);
      var opts = {
        urlInfo:      urlInfo,
        siteConfig:   options.siteConfig,
        urlData:      urlData,
        updateUISiteConfigOnChange: true
      }

      var urlProperties = { url: urlInfo.url };
      var uiURLInfo = new UIProperties({ bean: urlProperties });
      var northConfig = { };
      this.set('north', uiURLInfo, northConfig);

      var westConfig = { width: "500px"};
      var uiUrlAndXhtmlTabs = new UITabbedPane();
      uiUrlAndXhtmlTabs.addTab("url", "URL", new UIURLAnalyzer(opts), false, true);
      uiUrlAndXhtmlTabs.addTab("extractConfig", "Extract Config", new UIExtractConfig(opts), false, false);
      this.set('west', uiUrlAndXhtmlTabs, westConfig);

      var centerConfig = {};
      this.set('center', new UIXhtmlIFrame(opts), centerConfig);
    }
  });

  return UIWebPageAnalyzer ;
});
