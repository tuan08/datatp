define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'ui/UIUtil',
  'plugins/crawler/Rest',
], function($, _, Backbone, UIBean, UITable, UIUtil, Rest) {
  var UISiteConfigList = UITable.extend({
    label: "Site Config List",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "onNew", icon: "add", label: "New", 
              onClick: function(thisTable) { 
                thisTable.onAddBean() ;
              } 
            }
          ]
        }
      },
      
      bean: {
        label: 'Site Config',
        fields: [
          { 
            field: "hostname",   label: "Hostname", toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var siteConfig = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(siteConfig)) ;
              var uiSiteConfigScreen = UIUtil.getAncestorOfType(thisTable, 'UISiteConfigScreen');
              uiSiteConfigScreen.addSiteConfigTab(siteConfig);
            }
          },
          { 
            field: "group",   label: "Group", toggled: true, filterable: true
          },
          { 
            field: "status",   label: "Status", toggled: true, filterable: true
          },
          { 
            field: "injectUrl",   label: "Inject URL", toggled: true, filterable: true, multiple: true
          },
          { 
            field: "crawlSubDomain",   label: "Crawl Subdomain", toggled: true, filterable: true,
            select: {
              getOptions: function(field, bean) {
                var options = [
                  { label: 'True', value: true },
                  { label: 'False', value: false }
                ];
                return options ;
              }
            }
          },
          { 
            field: "crawlDeep",   label: "Crawl Deep", toggled: true, filterable: true
          },
          { 
            field: "maxConnection",   label: "Max Connection", toggled: true, filterable: true
          },
          { 
            field: "language",   label: "Language", toggled: true, filterable: true
          },
          { 
            field: "description",   label: "Description", toggled: true, filterable: true
          },
        ],
        actions:[
          {
            icon: "delete", label: "Delete",
            onClick: function(thisTable, row) { 
              thisTable.markDeletedItemOnCurrentPage(row) ;
              console.log('Mark delete row ' + row);
            }
          }
        ]
      }
    },

    onInit: function(options) {
      var siteConfigs = Rest.site.getSiteConfigs() ;
      this.setBeans(siteConfigs) ;
    }
  });
  
  return UISiteConfigList ;
});
