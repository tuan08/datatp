define([
  'ui/UITemplateLayout',
  'ui/bean/UIBean',
  'ui/bean/UIArrayBean',
  'ui/bean/UIComplexBean',
  'ui/bean/UIArrayComplexBean',
  'ui/UIBreadcumbs',
  'plugins/crawler/site/UISiteAnalyzer',
  'plugins/crawler/site/model'
], function(UITemplateLayout, UIBean, UIArrayBean, UIComplexBean, UIArrayComplexBean, UIBreadcumbs, UISiteAnalyzer,  model) {
  var UISiteConfigGeneric = UIBean.extend({

    config: { header: "Site Config Generic" },
    
    setSiteConfig: function(siteConfig) {
      this.set(model.site.config.SiteConfigGeneric, siteConfig);
      return this;
    }
  });

  var UIWebpageTypePattern = UIArrayBean.extend({
    config: { 
      header: "Webpage Type Pattern",
      label: function(bean, idx) { return "WP Pattern " + (idx + 1); }
    },
    
    setSiteConfig: function(siteConfig) {
      this.set(model.site.config.WebpageTypePattern, siteConfig.webPageTypePatterns);
      return this;
    },

    createDefaultBean: function() { return { type: "uncategorized", pattern: [] }; }
  });

  var UIExtractConfigBasic = UIBean.extend({
    config: { header: "Extract Config" },
    
    setExtractConfig: function(extractConfig) {
      this.set(model.site.config.ExtractConfig, extractConfig);
      return this;
    }
  });

  var UIExtractConfigXPath = UIArrayBean.extend({
    config: {
      header: "Extract XPath",
      layout: 'table'
    },
    
    setExtractConfig: function(extractConfig) {
      var xpaths = extractConfig.extractXPath;
      if(xpaths == null) xpaths = [];
      this.set(model.site.config.ExtractConfigXPath, xpaths);
      return this;
    },

    createDefaultBean: function() { return { }; }
  });

  var UIExtractConfig = UIComplexBean.extend({
    _template: _.template(`
      <div style="border: 1px solid #ddd; margin-top: 10px">
        <h6 class="box-border-bottom" style="padding: 5px">Complex Bean Mapping Demo</h6>
        <div>
          <div name="UIExtractConfigBasic"  class="box-display-ib box-valign-top" style="width: 40%" />
          <div name="UIExtractConfigXPath" class="box-display-ib box-valign-top" style="width: calc(60% - 5px)" />
        </div>
      </div>
    `),
    
    set: function(config) {
      console.printJSON(config);
      var uiBasic = new UIExtractConfigBasic().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigBasic", uiBasic) ;

      var uiXPaths = new UIExtractConfigXPath().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigXPath", uiXPaths) ;
      return this;
    }
  });

  var UIExtractConfigs = UIArrayComplexBean.extend({
    config: {
      UIComplexBean: UIExtractConfig,
      label: function(idx) { return "Extract " + (idx + 1); }
    },

    createDefaultBean: function() { return { }; }
  });

  var UISiteConfig = UITemplateLayout.extend({
    _template: _.template(`
      <div>
        <div class="box-layout-left-right box-border-bottom">
          <h6><%=label%></h6>
          <div>
            <a class="ui-action onSave">Save</a>
            <a class="ui-action onAnalyze">Analyzer</a>
          </div>
        </div>

        <div>
          <div name="UISiteConfigGeneric"  class="box-display-ib box-valign-top" style="width: 40%" />
          <div name="UIWebpageTypePattern" class="box-display-ib box-valign-top" style="width: calc(60% - 5px)" />
        </div>
        <div name="UIExtractConfigs" />
      <div>
    `),
    events: {
      "click .onSave": "onSave",
      "click .onAnalyze": "onAnalyze"
    },
    
    onSave: function(evt) {
      console.log("on save....");
    },

    onAnalyze: function(evt) {
      var uiBreadcumbs = this.getAncestorOfType("UISiteConfigBreadcumbs") ;
      uiBreadcumbs.push(new UISiteAnalyzer().configure(this.siteConfig));
    },


    onInit: function(options) {
      var siteConfig = options.siteConfig
      if(siteConfig.webPageTypePatterns == null) siteConfig.webPageTypePatterns = [];
      this.label = siteConfig.hostname;
      this.siteConfig = siteConfig;

      this.__setUIComponent('UISiteConfigGeneric', new UISiteConfigGeneric().setSiteConfig(siteConfig));
      this.__setUIComponent('UIWebpageTypePattern', new UIWebpageTypePattern().setSiteConfig(siteConfig));
      this.__setUIComponent('UIExtractConfigs', new UIExtractConfigs().set(siteConfig.extractConfig));
    }
  });

  var UISiteConfigBreadcumbs = UIBreadcumbs.extend({
    type:  "UISiteConfigBreadcumbs",

    onInit: function(options) {
      this.uiSiteConfig = new UISiteConfig(options) ;
      this.push(this.uiSiteConfig);
    },

    onChangeSiteConfig: function(siteConfig) {
    }
  });

  return UISiteConfigBreadcumbs;
});
