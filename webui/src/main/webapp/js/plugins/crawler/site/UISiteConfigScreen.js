define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UIUpload',
  'ui/UIDialog',
  'ui/bean/UITable',
  'plugins/crawler/site/UISiteConfigBreadcumbs',
  'plugins/crawler/model',
  'plugins/crawler/Rest',
], function($, _, Backbone, UITabbedPane, UIUpload, UIDialog, UITable, UISiteConfigBreadcumbs, model, Rest) {
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
    label: "Site Configs", 

    config: {
      control: { header: "Site"},
      table: { header: "Site Configurations"},
      actions: {
        toolbar: {
          newConfig: {
            label: "New",
            onClick: function(uiTable) { 
            }
          },
          importConfig: {
            label: "Import",
            onClick: function(uiTable) { uiTable.onImport(); }
          },
          exportConfig: {
            label: "Export",
            onClick: function(uiTable) { window.open(Rest.site.exportURL(), "export"); }
          }
        },

        bean: {
          editConfig: {
            label: "Edit",
            onClick: function(uiTable, beanState) {
              var siteConfig = beanState.bean ;
              var uiSiteConfigScreen = uiTable.getAncestorOfType('UISiteConfigScreen');
              uiSiteConfigScreen.addSiteConfigTab(siteConfig);
            }
          },
          rmConfig: {
            label: "Del",
            onClick: function(uiTable, beanState) {
              var siteConfig = beanState.bean ;
              Rest.site.remove(siteConfig.group, siteConfig.hostname);
              uiTable.removeBeanState(beanState, true) ;
            }
          }
        }
      }
    },
    
    onInit: function(options) {
      var siteConfigs = Rest.site.getSiteConfigs() ;
      this.addDefaultControlPluginUI();
      this.set(model.site.config.SiteConfigGeneric , siteConfigs);
    },

    onImport: function() {
      var config = {
        title: "Import",
        width: "400px", height: "400px"
      }
      var options = { uiSiteConfigList: this };
      UIDialog.activate(new UIImport(options), config) ;
    },

    onImportSuccess: function() {
      UIDialog.close() ;
      var siteConfigs = Rest.site.getSiteConfigs() ;
      this.setBeans(siteConfigs, true) ;
    },
  }) ;

  var UISiteConfigScreen = UITabbedPane.extend({
    type: 'UISiteConfigScreen',
    label: 'Site Configs',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      this.addTab("siteConfigList", "Site Configs", new UISiteConfigList(), false, true);
    },
    
    addSiteConfigTab: function(siteConfig) {
      var name = siteConfig.hostname;
      this.addTab(name, name, new UISiteConfigBreadcumbs().conf(siteConfig), true, true);
      this.render();
    }
  });

  return UISiteConfigScreen ;
});
