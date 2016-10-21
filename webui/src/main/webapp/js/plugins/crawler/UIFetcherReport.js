define([
  'ui/UICollapsible',
  'ui/UIProperties',
  'ui/UIBorderLayout',
  'ui/UITabbedPane',
  'ui/table/UITable',
  'plugins/crawler/model',
  'plugins/crawler/Rest'
], function(UICollabsible, UIProperties, UIBorderLayout, UITabbedPane, UITable, model, Rest) {

  var UIURLFetcher = UITable.extend({
    label: "URL Fetcher", 

    config: {
      control: { header: "Control"},
      table: { header: "URL Fetcher Report", width: "450px"},
    },
    
    onRefresh: function(fetcherReport) {
      this.set(model.fetcher.URLFetcherReport, fetcherReport.urlFetcherReport);
      return this;
    }
  }) ;

  var UIURLFetcherMetrics = UITable.extend({
    label: "URL Fetcher Metrics", 

    config: {
      control: { header: "Control"},
      table: { header: "URL Fetcher Metrics" },
    },
    
    onRefresh: function(fetcherReport) {
      var allMetrics = [];
      for(var i = 0; i < fetcherReport.urlFetcherReport.length; i++) {
        var urlFetcherMetric = fetcherReport.urlFetcherReport[i].urlFetcherMetric;
        var metrics = urlFetcherMetric.metrics;
        for(var j = 0; j < metrics.length; j++) {
          var metric     = metrics[j];
          metric.fetcher = urlFetcherMetric.name;
          allMetrics.push(metric);
        }
      }
      var groupByFields = ["fetcher", "category"];
      this.setTableGroupByFields(groupByFields, false);
      this.setTableView('groupby', false);
      this.set(model.fetcher.URLFetcherMetrics, allMetrics);
      return this;
    }
  }) ;
  

  var UIURLDatum = UITable.extend({
    label: "URLDatum", 

    config: {
      control: { header: "Control"},
      table: { header: "UrlDatum" },
    },
    
    onRefresh: function(fetcherReport) {
      var allRecentFetchUrls = [];
      for(var i = 0; i < fetcherReport.urlFetcherReport.length; i++) {
        var urlFetcherMetric = fetcherReport.urlFetcherReport[i].urlFetcherMetric;
        var recentFetchUrls = urlFetcherMetric.recentFetchUrls;
        for(var j = 0; j < recentFetchUrls.length; j++) {
          var url = recentFetchUrls[j];
          url.fetcher = urlFetcherMetric.name;
          allRecentFetchUrls.push(url);
        }
      }
      var groupByFields = ["fetcher"];
      this.setTableGroupByFields(groupByFields, false);
      this.setTableView('groupby', false);
      this.set(model.urldb.URLDatum, allRecentFetchUrls);
      return this;
    }
  }) ;

  var UIFetcherStatus = UICollabsible.extend({
    label: "Fetcher Status", 
    config: {
      actions: [
        { 
          action: "refresh", label: "Refesh",
          onClick: function(thisUI) {
            var uiReport = thisUI.getAncestorOfType("UIFetcherReport");
            uiReport.onRefresh();
            uiReport.render();
          }
        },
      ]
    },

    onRefresh: function(fetcherReport) {
      this.clear();

      var uiFetcherStatus = new UIProperties().conf({ header: "Fetcher Status"});
      uiFetcherStatus.label = "Fetcher Status";
      uiFetcherStatus.setBean(fetcherReport.status);
      this.add(uiFetcherStatus);

      var uiURLFetchQueueInfo = new UIProperties().conf({ header: "Queue Info"});
      uiURLFetchQueueInfo.label = "Queue Info";
      uiURLFetchQueueInfo.setBean(fetcherReport.urlFetchQueueReport);
      this.add(uiURLFetchQueueInfo);
      return this;
    },
  }) ;

  var UIFetcherReportTabbedPane = UITabbedPane.extend({

    config: {
      style: "ui-round-tabs",
      tabs: [
        { 
          label: "URL Fetcher",  name: "urlFetcher",
          onSelect: function(thisUI, tabConfig) {
            var ui = new UIURLFetcher().onRefresh(thisUI.fetcherReport);
            thisUI.setSelectedTabUIComponent(tabConfig.name, ui) ;
          }
        },
        { 
          label: "URL Fetcher Metrics",  name: "urlFetcherMetrics",
          onSelect: function(thisUI, tabConfig) {
            var ui = new UIURLFetcherMetrics().onRefresh(thisUI.fetcherReport);
            thisUI.setSelectedTabUIComponent(tabConfig.name, ui) ;
          }
        },
        { 
          label: "Recent Fetch Urls",  name: "recentFetchUrls",
          onSelect: function(thisUI, tabConfig) {
            var ui = new UIURLDatum().onRefresh(thisUI.fetcherReport);
            thisUI.setSelectedTabUIComponent(tabConfig.name, ui) ;
          }
        }
      ]
    },
    
    setFetcherReport: function(report) {
      this.fetcherReport = report;
      return this;
    }

  });


  var UIFetcherReport = UIBorderLayout.extend({
    type: 'UIFetcherReport',
    label: 'Fetcher Report',

    config: {
    },
    
    init: function(fetcherReportId) {
      this.fetcherReportId = fetcherReportId;

      var westConfig = { width: "450px"};
      this.uiFetcherStatus = new UIFetcherStatus();
      this.setUI('west', this.uiFetcherStatus, westConfig);

      var centerConfig = {};
      this.uiFetcherReportTabbedPane = new UIFetcherReportTabbedPane();
      this.setUI('center', this.uiFetcherReportTabbedPane, centerConfig);

      this.onRefresh();
      return this;
    },

    onRefresh: function() {
      var fetcherReport = Rest.fetcher.getFetcherReport(this.fetcherReportId);
      this.uiFetcherStatus.onRefresh(fetcherReport);
      this.uiFetcherReportTabbedPane.setFetcherReport(fetcherReport);
    }
  }) ;

  return UIFetcherReport ;
});
