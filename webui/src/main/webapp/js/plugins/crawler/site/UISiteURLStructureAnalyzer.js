define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITableTree',
  'plugins/crawler/site/UIURLStructureAnalyzer',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITableTree, UIURLStructureAnalyzer, Rest) {

  var UISiteURLStructure = UITableTree.extend({
    label: "Site URL Structure",

    config: {
      bean: {
        label: 'URL Structure',
        fields: [
          { 
            field: "domain", label: "Domain", defaultValue: '', toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(bean)) ;
            }
          },
          { field: "directory", label: "Directory", toggled: true },
          { 
            field: "url", label: "URL", toggled: true,
            custom: {
              getDisplay: function(bean) { 
                if(bean.url) return bean.url.url; 
                return "";
              }
            }
          }
        ],
        actions:[
          {
            icon: "edit", label: "Analyze",
            onClick: function(thisTable, node) { 
              var uiSiteConfig = thisTable.getAncestorOfType("UISiteConfig") ;
              var options = {
                siteConfig: thisTable.siteConfig,
                urlInfo: node.bean.url
              };
              uiSiteConfig.push(new UIURLStructureAnalyzer(options));
            }
          }
        ]
      }
    },

    onInit: function(options) {
      this.siteConfig   = options.siteConfig;
      var urlSiteStructure = options.urlSiteStructure; 
      var domainStructures = urlSiteStructure.domainStructures;
      for (var name in domainStructures) {
         var domainStructure = domainStructures[name];
        this._addDomain(domainStructure);
      }
    },
    
    _addDomain: function(domainStructure) {
      var domain = { domain: domainStructure.domain } ;
      var domainNode = this.addNode(domain);
      domainNode.setCollapse(false);
      domainNode.setDisableAction(true);

      var directoryStructures = domainStructure.directoryStructure;
      for (var path in directoryStructures) {
        var directoryStructure = directoryStructures[path];
        this._addDirectory(domainNode, directoryStructure) ;
      }
    },

    _addDirectory: function(domainNode, directoryStructure) {
      var directoryNode = domainNode.addChild(
        { domain: "", directory: directoryStructure.directory }
      );
      directoryNode.setCollapse(false);
      directoryNode.setDisableAction(true);

      var urls = directoryStructure.urls;
      for(var i = 0; i < urls.length; i++) {
        var url = urls[i]
        var urlNode =  directoryNode.addChild(
          { domain: "", directory: '', url: url }
        );
      }
    }
  });
  
  var UISiteURLStructureAnalyzer = UICollabsible.extend({
    label: "Site URL Analyzer", 
    config: {
      actions: [
        {
          action: "analyze", label: "Reanalyze",
          onClick: function(thisUI) { thisUI.analyze(); }
        },
        {
          action: "refresh", label: "Refresh",
          onClick: function(thisUI) { thisUI.refresh(); }
        }
      ]
    },

    onInit: function(options) {
      this.siteConfig = options.siteConfig;
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(this.siteConfig, 100, false);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
    },

    refresh: function(urlSiteStructure) {
      this.clear();
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(this.siteConfig, 100, false);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
      this.render();
    },

    analyze: function(urlSiteStructure) {
      this.clear();
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(this.siteConfig, 100, true);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
      this.render();
    },
    
  }) ;

  return UISiteURLStructureAnalyzer ;
});
