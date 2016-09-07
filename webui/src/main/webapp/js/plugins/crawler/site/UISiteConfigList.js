define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'ui/UIUpload',
  'ui/UIPopup',
  'plugins/crawler/Rest',
], function($, _, Backbone, UIBean, UITable, UIUpload, UIPopup, Rest) {
  var UIImport = UIUpload.extend({
    label: 'Import',

    config: {
      label: "Import",
      serviceUrl: "/crawler/site/import",
      onSuccess: function(thisUI) {
        console.log("on upload success");
      },

      onError: function(thisUI) {
        console.log("on upload error");
      }
    },

    onInit: function(options) {
    }
  });

  var UISiteConfigList = UITable.extend({
    label: "Site Config List",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "new", label: "New", 
              onClick: function(thisTable) { 
                thisTable.onAddBean() ;
              } 
            },
            {
              action: "import", label: "Import", 
              onClick: function(thisTable) { 
                var popupConfig = { title: "Import", minWidth: 400, modal: true} ;
                UIPopup.activate(new UIImport(), popupConfig) ;
              } 
            },
            {
              action: "export",  label: "Export", 
              onClick: function(thisTable) { 
                window.open(Rest.site.exportURL(), "export");
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
              var uiSiteConfigScreen = thisTable.getAncestorOfType('UISiteConfigScreen');
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
