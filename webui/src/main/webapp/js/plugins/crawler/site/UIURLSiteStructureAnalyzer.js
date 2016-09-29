define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITableTree',
  'plugins/crawler/site/UIWebPageAnalyzer',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITableTree, UIWebPageAnalyzer, Rest) {

  var UISiteURLStructure = UITableTree.extend({
    label: "URL Site Structure",

    config: {
      bean: {
        label: 'URL Structure',
        fields: [
          { 
            field: "domain", label: "Domain", defaultValue: '', toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
            }
          },
          { field: "category", label: "Category", toggled: true },
          { 
            field: "url", label: "URL", toggled: true,
            custom: {
              getDisplay: function(bean) { 
                if(bean.url) return bean.url.urlInfo.pathWithParams; 
                return "";
              }
            }
          },
          { 
            field: "extractEntityInfo", label: "Entity Info", toggled: true,
            custom: {
              getDisplay: function(bean) { 
                if(bean.url) return bean.url.extractEntityInfo; 
                return "";
              }
            }
          }
        ],
        actions:[
          {
            icon: "edit", label: "Analyse",
            onClick: function(thisTable, node) { 
              var uiSiteConfig = thisTable.getAncestorOfType("UISiteConfig") ;
              var options = {
                siteConfig: thisTable.siteConfig,
                urlAnalysis: node.bean.url
              };
              uiSiteConfig.push(new UIWebPageAnalyzer(options));
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

      var categoryStructures = domainStructure.categoryStructure;
      for (var path in categoryStructures) {
        var categoryStructure = categoryStructures[path];
        this._addCategory(domainNode, categoryStructure) ;
      }
    },

    _addCategory: function(domainNode, categoryStructure) {
      var categoryNode = domainNode.addChild(
        { domain: "", category: categoryStructure.category }
      );
      if('ignore' == categoryStructure.category) {
        categoryNode.setCollapse(true);
      } else {
        categoryNode.setCollapse(false);
      }
      categoryNode.setDisableAction(true);

      var urls = categoryStructure.urls;
      for(var i = 0; i < urls.length; i++) {
        var url = urls[i]
        var urlNode =  categoryNode.addChild(
          { domain: "", category: '', url: url }
        );
      }
    }
  });
  
  var UISiteURLStructureAnalyzer = UICollabsible.extend({
    label: "Site URL Analyzer", 
    config: {
      actions: [
        {
          action: "recrawl", label: "Recrawl",
          onClick: function(thisUI) { thisUI.recrawl(); }
        },
        {
          action: "analyze", label: "Reanalyze",
          onClick: function(thisUI) { thisUI.reanalyse(); }
        },
        {
          action: "refresh", label: "Refresh",
          onClick: function(thisUI) { thisUI.refresh(); }
        }
      ]
    },

    onInit: function(options) {
      this.siteConfig = options.siteConfig;
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(this.siteConfig, 250, false);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
    },

    refresh: function(urlSiteStructure) {
      this.clear();
      var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(this.siteConfig, 250, false);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
      this.render();
    },

    reanalyse: function(urlSiteStructure) {
      this.clear();
      var urlSiteStructure = Rest.site.reanalyseURLSiteStructure(this.siteConfig, 250);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
      this.render();
    },

    recrawl: function(urlSiteStructure) {
      this.clear();
      var urlSiteStructure = Rest.site.recrawlURLSiteStructure(this.siteConfig, 250);
      var options = { siteConfig: this.siteConfig, urlSiteStructure: urlSiteStructure };
      this.add(new UISiteURLStructure(options));
      this.render();
    },
    
  }) ;

  return UISiteURLStructureAnalyzer ;
});
