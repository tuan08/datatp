define([
  'ui/UITemplateLayout',
  'ui/bean/UIBean',
  'ui/bean/UIArrayBean',
  'ui/bean/UIComplexBean',
  'ui/bean/UIArrayComplexBean',
  'ui/UIBreadcumbs',
  'plugins/crawler/site/UISiteAnalyzer',
  'plugins/crawler/site/uicomp',
  'plugins/crawler/model',
  'plugins/crawler/Rest'
], function(UITemplateLayout, UIBean, UIArrayBean, UIComplexBean, UIArrayComplexBean, UIBreadcumbs, UISiteAnalyzer, uicomp,  model, Rest) {
  var UISiteConfigGeneric = UIBean.extend({

    config: { header: "Site Config Generic" },
    
    setSiteConfig: function(siteConfig) {
      this.set(model.site.config.SiteConfigGeneric, siteConfig);
      return this;
    }
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
      Rest.site.save(this.siteConfig);
    },

    onAnalyze: function(evt) {
      var uiBreadcumbs = this.getAncestorOfType("UISiteConfigBreadcumbs") ;
      uiBreadcumbs.push(new UISiteAnalyzer().configure(this.siteConfig));
    },


    conf: function(siteConfig) {
      if(siteConfig.webPageTypePatterns == null) siteConfig.webPageTypePatterns = [];
      this.label = siteConfig.hostname;
      this.siteConfig = siteConfig;
      this.onModifiedModel();
      return this;
    },

    onModifiedModel: function() {
      var siteConfig = this.siteConfig;
      this.__setUIComponent('UISiteConfigGeneric', new UISiteConfigGeneric().setSiteConfig(siteConfig));
      this.__setUIComponent('UIWebpageTypePattern', new uicomp.site.UIWebpageTypePattern().setSiteConfig(siteConfig));
      this.__setUIComponent('UIExtractConfigs', new uicomp.site.UIExtractConfigs().set(siteConfig.extractConfig));
    },

    broadcastSiteConfigChange: function(siteConfig) {
      this.siteConfig = siteConfig;
      this.markModifiedModel(true);
    }
  });

  var UISiteConfigBreadcumbs = UIBreadcumbs.extend({
    type:  "UISiteConfigBreadcumbs",

    conf: function(siteConfig) {
      this.uiSiteConfig = new UISiteConfig().conf(siteConfig) ;
      this.push(this.uiSiteConfig);
      return this;
    },

    broadcastSiteConfigChange: function(siteConfig) {
      this.uiSiteConfig.broadcastSiteConfigChange(siteConfig) ;
    }
  });

  return UISiteConfigBreadcumbs;
});
