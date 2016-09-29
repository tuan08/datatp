define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITable',
  'ui/UIUpload',
  'ui/UIPopup',
  'plugins/crawler/Rest',
], function($, _, Backbone, UITable, UIUpload, UIPopup, Rest) {
  var UIImport = UIUpload.extend({
    label: 'Import',

    config: {
      label: "Import",
      serviceUrl: "/crawler/site/import",
      onSuccess: function(thisUI, data) {
        thisUI.uiSiteConfigList.onImportSuccess();
      },

      onError: function(thisUI, data) {
        console.log("on upload error");
      }
    },

    onInit: function(options) {
      this.uiSiteConfigList = options.uiSiteConfigList;
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
              onClick: function(thisTable) { thisTable.onAddBean() ; } 
            },
            {
              action: "import", label: "Import", 
              onClick: function(thisTable) { thisTable.onImport(); } 
            },
            {
              action: "export",  label: "Export", 
              onClick: function(thisTable) { window.open(Rest.site.exportURL(), "export"); } 
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
              var uiSiteConfigScreen = thisTable.getAncestorOfType('UISiteConfigScreen');
              uiSiteConfigScreen.addSiteConfigTab(siteConfig);
            }
          },
          { field: "group",   label: "Group", toggled: true, filterable: true },
          { field: "status",   label: "Status", toggled: true, filterable: true },
          { field: "injectUrl",   label: "Inject URL", toggled: true, filterable: true, multiple: true },
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
          { field: "crawlDeep",   label: "Crawl Deep", toggled: true, filterable: true },
          { field: "maxConnection",   label: "Max Connection", toggled: true, filterable: true },
          { field: "language",   label: "Language", toggled: true, filterable: true },
          { field: "description",   label: "Description", toggled: true, filterable: true },
        ],
        actions:[
          {
            icon: "delete", label: "Delete",
            onClick: function(thisTable, row) { 
              var siteConfig = thisTable.getItemOnCurrentPage(row) ;
              Rest.site.remove(siteConfig.group, siteConfig.hostname);
              thisTable.removeItemOnCurrentPage(row) ;
            }
          }
        ]
      }
    },

    onInit: function(options) {
      var siteConfigs = Rest.site.getSiteConfigs() ;
      this.setBeans(siteConfigs) ;
    },

    onImport: function() {
      var popupConfig = { title: "Import", minWidth: 400, modal: true} ;
      var options = { uiSiteConfigList: this };
      UIPopup.activate(new UIImport(options), popupConfig) ;
    },

    onImportSuccess: function() {
      UIPopup.closePopup() ;
      var siteConfigs = Rest.site.getSiteConfigs() ;
      this.setBeans(siteConfigs) ;
      this.render();
    },

  });
  
  return UISiteConfigList ;
});
