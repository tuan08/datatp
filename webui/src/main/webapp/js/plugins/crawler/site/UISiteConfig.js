define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBreadcumbs',
  'ui/UIBean',
  'ui/UITable',
  'ui/UICollapsible',
  'plugins/crawler/site/UIURLPattern',
  'plugins/crawler/site/UIExtractConfig',
  'plugins/crawler/site/UIURLSiteStructureAnalyzer',
  'plugins/crawler/Rest'
], function($, _, Backbone, UIBreadcumbs, UIBean, UITable, UICollabsible, UIURLPattern, UIExtractConfig, UIURLSiteStructureAnalyzer, Rest) {
  var UISiteConfigGeneric = UIBean.extend({
    label: "Site Config Generic",
    config: {
      beans: {
        siteConfig: {
          name: 'siteConfig', label: 'Site Config',
          fields: [
            { field: "group",   label: "Group", required: true  },
            { field: "hostname",   label: "Hostname", required: true },
            { field: "status",   label: "Status" },
            { field: "injectUrl",   label: "Inject URL", multiple: true },
            { 
              field: "crawlSubDomain",   label: "Crawl Subdomain",
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
            { field: "crawlDeep",   label: "Crawl Deep" },
            { field: "maxConnection",   label: "Max Connection" },
            { field: "language",   label: "Language" },
            { field: "description",   label: "Description", textarea: {} }
          ],
          edit: {
            disable: false , 
            actions: [ ],
          },
          view: {
            actions: [ ]
          }
        }
      }
    }
  });

  var UISiteConfigCollabsible = UICollabsible.extend({
    label: "Site Config", 
    config: {
      actions: [
        {
          action: "save", label: "Save",
          onClick: function(thisUI) { 
            var siteConfig = thisUI.siteConfig;
            Rest.site.save(siteConfig);
          }
        },
        {
          action: "analyzer", label: "Analyzer",
          onClick: function(thisUI) { 
            var siteConfig = thisUI.siteConfig;
            var uiSiteConfig = thisUI.getAncestorOfType("UISiteConfig") ;
            uiSiteConfig.push(new UIURLSiteStructureAnalyzer({ siteConfig: siteConfig }));
          }
        },
        { 
          action: "back", label: "Back",
          onClick: function(thisUI) {
          }
        }
      ]
    },

    onInit: function(options) {
      this.onChangeSiteConfig(options.siteConfig);
    },

    onChangeSiteConfig: function(siteConfig) {
      this.clear();
      this.siteConfig = siteConfig;
      var uiSiteConfigGeneric = new UISiteConfigGeneric();
      uiSiteConfigGeneric.bind('siteConfig', siteConfig, true) ;
      uiSiteConfigGeneric.getBeanState('siteConfig').editMode = true ;

      var uiURLPattern = new UIURLPattern() ;
      if(this.siteConfig.urlPatterns == null) siteConfig.urlPatterns = [];
      uiURLPattern.setBeans(siteConfig.urlPatterns) ;

      var uiExtractConfig = new UIExtractConfig({siteConfig: siteConfig}) ;
      this.add(uiSiteConfigGeneric);
      this.add(uiURLPattern);
      this.add(uiExtractConfig);
    }
  }) ;

  var UISiteConfig = UIBreadcumbs.extend({
    type:  "UISiteConfig",

    onInit: function(options) {
      this.uiSiteConfigCollabsible = new UISiteConfigCollabsible({siteConfig: options.siteConfig}) ;
      this.push(this.uiSiteConfigCollabsible);
    },

    onChangeSiteConfig: function(siteConfig) {
      this.uiSiteConfigCollabsible.onChangeSiteConfig(siteConfig);
    }
  });

  return UISiteConfig ;
});
