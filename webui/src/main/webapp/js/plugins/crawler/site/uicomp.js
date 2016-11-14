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
      console.log("on field change: fieldName = " + fieldName + ", oldValue = " + oldValue + ", newValue = " + value);
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
      if(extractConfig.extractXPath == null) extractConfig.extractXPath = [];
      this.set(model.site.config.ExtractConfigXPath, extractConfig.extractXPath);
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

    getBean: function() { return this.extractConfig; },
    
    set: function(config) {
      this.extractConfig = config;
      var uiBasic = new UIExtractConfigBasic().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigBasic", uiBasic) ;

      this.uiExtractConfigXPath = new UIExtractConfigXPath().setExtractConfig(config);
      this.__setUIComponent("UIExtractConfigXPath", this.uiExtractConfigXPath) ;
      return this;
    },

    getUIExtractConfigXPath: function() { return this.uiExtractConfigXPath; },
    
    commitChange: function() { 
      this.uiExtractConfigXPath.commitChange(); 
    }
  });

  var UIExtractConfigs = UIBeanComplexArray.extend({
    config: {
      UIBeanComplex: UIExtractConfig,

      label: function(idx) { return "Extract " + (idx + 1); }
    },

    createDefaultBean: function() { return { }; },

    commitChange: function() { 
      var uiExtractConfigs = this.getUIBeanComplexes();
      for(var i = 0; i < uiExtractConfigs.length; i++) uiExtractConfigs[i].commitChange();
    }
  });

  var uicomp = {
    site: {
      UIWebpageTypePattern: UIWebpageTypePattern,
      UIExtractConfigs: UIExtractConfigs  
    }
  };


  return uicomp;
});
