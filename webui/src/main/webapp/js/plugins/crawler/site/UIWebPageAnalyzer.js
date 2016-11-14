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

      this.uiExtractConfigs =  new uicomp.site.UIExtractConfigs();
      var uiWebPageAnalyzer = this;
      this.uiExtractConfigs.onInitUIBeanComplex = function(uiExtractConfig) {
        var onSelectHighlight = function(uiComp, beanState) {
          var xpath = uiWebPageAnalyzer.uiXhtmlIFrame.getCurrentSelectXPath();
          if(xpath == null) return;
          var xpathSelectorExp = xpath.getJSoupXPathSelectorExp();
          beanState.addToArray("xpath", xpathSelectorExp);
          beanState.commitChange();
          uiComp.render();
        };
        var uiXPaths = uiExtractConfig.getUIExtractConfigXPath();
        uiXPaths.addAction("selectHighlight", "Sel", onSelectHighlight);
      };

      this.uiExtractConfigs.set(siteConfig.extractConfig);
      

      uiUrlAndXhtmlTabs.addTab("extractConfigs", "Extract Configs", this.uiExtractConfigs, false, false);
      this.setUI('west', uiUrlAndXhtmlTabs, westConfig);

      var uiXhtmlContentTabs = new UITabbedPane();
      this.uiXhtmlIFrame = new UIXhtmlIFrame(opts);
      uiXhtmlContentTabs.addTab("xhtml", "Xhtml", this.uiXhtmlIFrame, false, true);
      var uiWebPageAnalysis = new UIContent({ content: JSON.stringify(urlData.webPageAnalysis, null, 2) });
      uiXhtmlContentTabs.addTab("uiWebPageAnalysis", "WebPage Analysis", uiWebPageAnalysis, false, false);
      this.setUI('center', uiXhtmlContentTabs , {}/*center config*/);
    },

    onBreadcumbsRemove: function() {
      this.uiExtractConfigs.commitChange();
    }
  });

  return UIWebPageAnalyzer ;
});
