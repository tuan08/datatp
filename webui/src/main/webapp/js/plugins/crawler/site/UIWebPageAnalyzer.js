define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContent',
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  'plugins/crawler/site/UIURLAnalyzer',
  'plugins/crawler/site/UIXhtmlIFrame',
  'plugins/crawler/site/uicomp',
  'plugins/crawler/Rest'
], function($, _, Backbone, UIContent, UIBorderLayout, UITabbedPane, UIURLAnalyzer, UIXhtmlIFrame, uicomp, Rest) {
  var UIWebPageAnalyzer = UIBorderLayout.extend({
    label: 'WebPage Analyzer',

    config: {
    },
    
    onInit: function(options) {
      var urlAnalysis = options.urlAnalysis;
      var urlInfo = urlAnalysis.urlInfo;
      var urlData =  Rest.site.getAnalyzedURLData(urlInfo.url);
      var siteConfig =  options.siteConfig;

      var opts = {
        urlInfo:      urlInfo,
        siteConfig:   options.siteConfig,
        urlData:      urlData,
        updateUISiteConfigOnChange: true
      }

      var westConfig = { width: "500px"};
      var uiUrlAndXhtmlTabs = new UITabbedPane();
      uiUrlAndXhtmlTabs.addTab("url", "URL", new UIURLAnalyzer(opts), false, true);
      var uiExtractConfigs =  new uicomp.site.UIExtractConfigs().set(siteConfig.extractConfig);
      uiUrlAndXhtmlTabs.addTab("extractConfigs", "Extract Configs", uiExtractConfigs, false, false);
      this.setUI('west', uiUrlAndXhtmlTabs, westConfig);

      var centerConfig = {};
      this.setUI('center', new UIXhtmlIFrame(opts), centerConfig);
    }
  });

  return UIWebPageAnalyzer ;
});
