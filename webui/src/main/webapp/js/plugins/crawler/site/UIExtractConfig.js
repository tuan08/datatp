define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIContainer',
  'ui/UIBean',
  'ui/UITable'
], function($, _, Backbone, UIContainer, UIBean, UITable) {
  
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

    onSaveBeanCallback: function(thisTable, row, bean) {
      var beans = thisTable.commitChange();
    },

    onDeleteBeanCallback: function(thisTable, row) {
      thisTable.removeItemOnCurrentPage(row);
    },
    
    addExtractXPath: function(extractXPath) {
      this.onAddBeanWith(extractXPath, this.onSaveBeanCallback);
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

      this.uiExtractXPath = new UIExtractXPath();
      if(extractConfig.extractXPath == null) extractConfig.extractXPath = [] ;
      this.uiExtractXPath.setBeans(extractConfig.extractXPath) ;
      this.add(this.uiExtractXPath) ;
    },
    
    addExtractXPath: function(extractXPath) {
      this.uiExtractXPath.addExtractXPath(extractXPath);
    } 
  }) ;
  
  return UIExtractConfig ;
});
