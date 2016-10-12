define([
  'ui/UITemplateLayout',
  'ui/bean/UIBean',
  'ui/bean/UIBeanArray',
  'ui/bean/UIBeanComplex',
  'ui/bean/UIBeanComplexArray',
  'ui/UIBreadcumbs',
  'plugins/crawler/site/UISiteAnalyzer',
  'plugins/crawler/model'
], function(UITemplateLayout, UIBean, UIBeanArray, UIBeanComplex, UIBeanComplexArray, UIBreadcumbs, UISiteAnalyzer,  model) {
  var UIWebpageTypePattern = UIBeanArray.extend({
    config: { 
      header: "Webpage Type Pattern",
      label: function(bean, idx) { return bean.type; }
    },

    onFieldChange: function(bean, fieldName, oldValue, value) {
      this.getAncestorOfType("UISiteConfigBreadcumbs").broadcastSiteConfigChange(this.siteConfig) ;
    },
    
    setSiteConfig: function(siteConfig) {
      this.siteConfig = siteConfig;
      this.set(model.site.config.WebpageTypePattern, siteConfig.webPageTypePatterns);
      return this;
    },

    createDefaultBean: function() { return { type: "uncategorized", pattern: [] }; }
  });

  var UIExtractConfigBasic = UIBean.extend({
    config: { 
      header: "Extract Config" 
    },
    
    setExtractConfig: function(extractConfig) {
      this.set(model.site.config.ExtractConfig, extractConfig);
      return this;
    }
  });

  var UIExtractConfigXPath = UIBeanArray.extend({
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

  var UIExtractConfig = UIBeanComplex.extend({
    _template: _.template(`
      <div style="border: 1px solid #ddd; margin-top: 10px">
        <div name="UIExtractConfigBasic" />
        <div name="UIExtractConfigXPath" />
      </div>
    `),
    
    set: function(config) {
      var uiBasic = new UIExtractConfigBasic().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigBasic", uiBasic) ;

      var uiXPaths = new UIExtractConfigXPath().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigXPath", uiXPaths) ;
      return this;
    }
  });

  var UIExtractConfigs = UIBeanComplexArray.extend({
    config: {
      UIBeanComplex: UIExtractConfig,
      label: function(idx) { return "Extract " + (idx + 1); }
    },

    createDefaultBean: function() { return { }; }
  });

  var uicomp = {
    site: {
      UIWebpageTypePattern: UIWebpageTypePattern,
      UIExtractConfigs: UIExtractConfigs  
    }
  };


  return uicomp;
});
