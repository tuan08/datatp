define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContainer',
  'ui/UIBean'
], function($, _, Backbone, UIContainer, UIBean) {
  
  var UIGeneric = UIBean.extend({
    label: "Generic Bean",
    config: {
      beans: {
        generic: {
          label: 'Bean',
          fields: [
            { 
              field: "name",   label: "Name", required: true,  
              validator: { name: 'empty', errorMsg: "custom error message" } 
            },
            { 
              field: "matchType",   label: "Match Type",
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'None', value: 'none' },
                    { label: 'Url',  value: 'url' },
                    { label: 'Title', value: 'title' }
                  ];
                  return options ;
                }
              }
            },
            { field: "matchPattern",  label: "Match Pattern", multiple: true },
            { 
              field: "extractAuto",  label: "Extract Auto",
              custom: {
                getDisplay: function(bean) { return bean.extractAuto ; },
                set: function(bean, obj) { 
                  if(bean.extractAuto == null) bean.extractAuto = [];
                  if(obj == null) bean.extractAuto = [];
                  else            bean.extractAuto.push(obj) ;
                },
                
                autocomplete: {
                  search: function(term, bean) {
                    var result = [
                      { value: 'article', label: "Article" },
                      { value: 'forum',   label: "Forum" }
                    ];
                    return result ;
                  }
                }
              }
            },
          ]
        }
      }
    }
  });

  var UIExtractXPath = UIBean.extend({
    label: "Extract XPath",
    config: {
      type: 'array',
      beans: {
        extractXPath: {
          label: 'Extract XPath',
          getLabel: function(bean) { return bean.name ; },
          fields: [
            { field: "name",   label: "Name", required: true },
            { field: "xpath",   label: "XPath", required: true }
          ]
        }
      }
    }
  });

  var UIExtractConfig = UIContainer.extend({
    label: "UIExtractConfig", 
    config: {
      actions: [ ]
    },
    
    onInit: function(options) {
      this.setHideHeader(true);
      this.setHideFooter(true);

      var siteConfig = options.siteConfig;
      var extractConfigArray = siteConfig.extractConfig;
      var extractConfig = extractConfigArray[0];

      var uiGenericBean = new UIGeneric() ;
      uiGenericBean.bind('generic', extractConfig, true) ;
      this.add(uiGenericBean) ;

      var uiExtractXPath = new UIExtractXPath();
      if(extractConfig.extractXPath == null) extractConfig.extractXPath = [] ;
      uiExtractXPath.bindArray('extractXPath', extractConfig.extractXPath) ;
      this.add(uiExtractXPath) ;
    }
  }) ;
  
  return UIExtractConfig ;
});
