define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/table/UITable',
  'plugins/crawler/site/UIWebPageAnalyzer',
  'plugins/crawler/model',
  'plugins/crawler/Rest',
], function($, _, Backbone, UITable, UIWebPageAnalyzer, model, Rest) {

  var UISiteAnalyzerCtrl = Backbone.View.extend({
    initialize: function (options) {
    },

    _template: _.template(`
      <div class="ui-card">
        <h6>Views</h6>
        <div class="content">
          <div><a class="ui-action onGroupByPageType">Group By Page Type</a></div>
          <div><a class="ui-action onGroupByDirectory">Group By Directory</a></div>
        </div>
      </div>
    `),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onGroupByPageType': 'onGroupByPageType',
      'click .onGroupByDirectory': 'onGroupByDirectory',
    },

    onGroupByPageType: function(evt) {
      var groupByFields = ["urlInfo.host", "pageType"];
      this.uiTable.setTableGroupByFields(groupByFields, false);
      this.uiTable.setTableView('groupby', true);
    },

    onGroupByDirectory: function(evt) {
      var groupByFields = ["urlInfo.host", "urlInfo.directory"];
      this.uiTable.setTableGroupByFields(groupByFields, false);
      this.uiTable.setTableView('groupby', true);
    },
  });

  var UISiteAnalyzer = UITable.extend({
    label: 'Site Structure Analyzer',

    config: {
      control: { header: "Site Analyzer Control"},
      table: { header: "Site URL Structure And Data"},
      actions: {
        toolbar: {
          reanalyze: {
            label: "Reanalyze",
            onClick: function(uiTable) { uiTable.onReanalyze(); }
          },
          refresh: {
            label: "Refresh",
            onClick: function(uiTable) { uiTable.onRefresh(); }
          }
        },

        bean: {
          more: {
            label: "More",
            onClick: function(uiTable, beanState) {
              uiTable.onAnalyzeURL(beanState.bean);
            }
          }
        }
      }
    },
    

    configure: function(siteConfig) {
      this.siteConfig = siteConfig;
      this.addDefaultControlPluginUI();
      this.addControlPluginUI("Control", new UISiteAnalyzerCtrl());
      this.set(model.site.analysis.URLAnalysis, []);
      this.autoRefresh();
      return this;
    },

    onRefresh: function() { this.autoRefresh(); },

    onReanalyze: function() {
      var urlSiteStructure = Rest.site.reanalyseURLSiteStructure(this.siteConfig, 250);
      this.setBeans(urlSiteStructure, true);
    },


    onAnalyzeURL: function(urlAnalysis) {
      var uiBreadcumbs = this.getAncestorOfType("UISiteConfigBreadcumbs") ;
      var options = {
        siteConfig: this.siteConfig,
        urlAnalysis: urlAnalysis
      };
      uiBreadcumbs.push(new UIWebPageAnalyzer(options));
    },

    autoRefresh: function() {
      var SIZE = 250 ;
      var repeatCount = 0;
      var thisUI = this;
      var refreshMethod = function() {
        var urlSiteStructure = Rest.site.getAnalyzedURLSiteStructure(thisUI.siteConfig, SIZE, false);
        thisUI.setBeans(urlSiteStructure, true);
        repeatCount++;
        if(urlSiteStructure.length < SIZE && repeatCount < 5) {
          setTimeout(refreshMethod, 1500);
        }
      }
      refreshMethod();
    },
  }) ;

  return  UISiteAnalyzer ;
});
