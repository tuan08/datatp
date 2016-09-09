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
              field: "name",   label: "Entity Name", required: true,  
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'content', value: 'content' },
                    { label: 'job',     value: 'job' },
                    { label: 'product', value: 'product' },
                    { label: 'comment', value: 'comment' }
                  ];
                  return options ;
                }
              }
            },
            { 
              field: "extractType",  label: "Extract Type", 
              select: {
                getOptions: function(field, bean) {
                  var options = [
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
            { field: "matchPattern",  label: "Match Pattern", multiple: true }
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
          { 
            field: "name",   label: "Name", required: true, toggled: true, filterable: true,
            select: {
              getOptions: function(field, bean) {
                var options = [
                  { label: 'Title',     value: 'title' },
                  { label: 'Description', value: 'description' },
                  { label: 'Content',     value: 'content' }
                ];
                return options ;
              }
            }
          },
          { field: "xpath",   label: "XPath", required: true, multiple: true, toggled: true, filterable: true  }
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
    
    addExtractXPath: function(newExtractXPath) {
      var extractXPath = this.getBeans();
      var merged = false;
      for(var i = 0; i < extractXPath.length; i++) {
        if(extractXPath[i].name == newExtractXPath.name) {
          extractXPath[i].xpath.push(newExtractXPath.xpath);
          merged = true;
          break;
        }
      }
      if(!merged) extractXPath.push(newExtractXPath); 
      this.setBeans(extractXPath);
      this.render();
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
