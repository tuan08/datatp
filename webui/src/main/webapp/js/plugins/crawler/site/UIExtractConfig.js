define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContainer',
  'ui/UIBean',
  'ui/UITable'
], function($, _, Backbone, UIContainer, UIBean, UITable) {
  
  var UIGeneric = UIBean.extend({
    label: "Generic",
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
                    { label: 'Any', value: 'any' },
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
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'none', value: 'none' },
                    { label: 'content', value: 'content' },
                    { label: 'article', value: 'article' },
                    { label: 'forum',   value: 'forum' },
                    { label: 'classified',   value: 'classified' },
                    { label: 'job',   value: 'job' },
                    { label: 'comment',   value: 'comment' }
                  ];
                  return options ;
                }
              }
            }
          ]
        }
      }
    }
  });

  var UIExtractXPath = UITable.extend({
    label: "Extract XPath",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "onNew", icon: "add", label: "New", 
              onClick: function(thisTable) { 
                thisTable.onAddBean(thisTable.onSaveBeanCallback) ; 
              } 
            }
          ]
        }
      },
      
      bean: {
        label: 'Extract XPath',
        fields: [
          { field: "name",   label: "Name", required: true, toggled: true, filterable: true, autocomplete: true },
          { field: "xpath",   label: "XPath", required: true, toggled: true, filterable: true  }
        ],
        actions:[
          {
            icon: "edit", label: "Edit",
            onClick: function(thisTable, row) { 
              thisTable.onEditBean(row, thisTable.onSaveBeanCallback) ;
            }
          },
          {
            icon: "delete", label: "Del",
            onClick: function(thisTable, row) { 
              thisTable.onDeleteBeanCallback(thisTable, row);
            }
          }
        ]
      }
    },

    configure: function(siteConfig, extractConfig, updateUISiteConfigOnChange) {
      this.siteConfig = siteConfig;
      this.setBeans(extractConfig.extractXPath) ;
      this.updateUISiteConfigOnChange = updateUISiteConfigOnChange;
    } ,

    onSaveBeanCallback: function(thisTable, row, bean) {
      thisTable.commitChange();
      if(thisTable.updateUISiteConfigOnChange) {
        var uiSiteConfig = thisTable.getAncestorOfType('UISiteConfig') ;
        uiSiteConfig.onChangeSiteConfig(thisTable.siteConfig);
      }
    },

    onDeleteBeanCallback: function(thisTable, row) {
      thisTable.removeItemOnCurrentPage(row);
      thisTable.commitChange();
      if(thisTable.updateUISiteConfigOnChange) {
        var uiSiteConfig = thisTable.getAncestorOfType('UISiteConfig') ;
        uiSiteConfig.onChangeSiteConfig(thisTable.siteConfig);
      }
    },
    
    addExtractXPath: function(extractXPath) {
      this.onAddBeanWith(extractXPath, this.onSaveBeanCallback);
    }
  });

  var UIExtractConfig = UIContainer.extend({
    label: "Extract Config", 
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

      this.uiExtractXPath = new UIExtractXPath();
      if(extractConfig.extractXPath == null) extractConfig.extractXPath = [] ;
      this.uiExtractXPath.configure(siteConfig, extractConfig, options.updateUISiteConfigOnChange) ;
      this.add(this.uiExtractXPath) ;
    },
    
    addExtractXPath: function(extractXPath) {
      this.uiExtractXPath.addExtractXPath(extractXPath);
    },

    getXPathConfigs: function() { return this.uiExtractXPath.getBeans(); } 
  }) ;
  
  return UIExtractConfig ;
});
