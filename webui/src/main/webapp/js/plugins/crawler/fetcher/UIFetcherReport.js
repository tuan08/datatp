define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITable',
  'ui/UIBean',
  'ui/UIBreadcumbs',
  'plugins/crawler/fetcher/UIURLFetcherMetric',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITable, UIBean, UIBreadcumbs, UIURLFetcherMetric, Rest) {
  var UIFetcherStatus = UIBean.extend({
    label: "Fetcher Status",
    config: {
      beans: {
        bean: {
          name: 'status', label: 'URL Scheduler Status',
          fields: [
            { field:  "id", label: "Id"},
            { field:  "host", label: "Host"},
            { field:  "status", label: "Status" }
          ],
          edit: { actions: [ ], },
          view: { actions: [ ] }
        }
      }
    }
  });

  var UIURLFetchQueueInfo = UIBean.extend({
    label: "Queue Info",
    config: {
      beans: {
        bean: {
          label: 'Queue Info',
          fields: [
            { field:  "inQueue", label: "In Queue"},
            { field:  "enqueue", label: "Enqueue"},
            { field:  "inDelayQueue", label: "In Delay Queue"},
            { field:  "enqueueDelay", label: "Enqueue Delay"},
          ],
          edit: { actions: [ ], },
          view: { actions: [ ] }
        }
      }
    }
  });

  var UIURLFetcherReport = UITable.extend({
    label: "URL Fetcher Report",

    config: {
      toolbar: {
        dflt: {
          actions: [ ]
        }
      },
      
      bean: {
        label: 'Fetcher Status',
        fields: [
          { field:  "name", label: "Name",  toggled: true, filterable: true },
          { field:  "status", label: "Status",  toggled: true, filterable: true },
          { 
            field:  "fetchCount", label: "Fetch Count",  toggled: true, filterable: true,
            custom: {
              getDisplay: function(bean) { return bean.urlFetcherMetric.fetchCount; }
            }
          }
        ],
        actions:[ 
          {
            icon: "more", label: "More",
            onClick: function(thisTable, row) { 
            }
          },
        ]
      }
    },

    onInit: function(options) {
    }
  });

  var UIFetcherReport = UICollabsible.extend({
    label: "Fetcher Report", 
    config: {
      actions: [
        { 
          action: "refresh", label: "Refesh",
          onClick: function(thisUI) {
            var fetcherReport = Rest.fetcher.getFetcherReport(thisUI.fetcherReportId);
            thisUI.onRefresh(fetcherReport);
            thisUI.render();
          }
        },
      ]
    },

    onInit: function(options) {
      this.fetcherReportId = options.fetcherReportId;
      var fetcherReport = Rest.fetcher.getFetcherReport(this.fetcherReportId);
      this.onRefresh(fetcherReport);   
    },


    onRefresh: function(fetcherReport) {
      this.clear();
       
      var uiFetcherStatus = new UIFetcherStatus();
      uiFetcherStatus.bind('bean', fetcherReport.status);
      uiFetcherStatus.setReadOnly(true);
      this.add(uiFetcherStatus);

      var uiURLFetchQueueInfo = new UIURLFetchQueueInfo();
      uiURLFetchQueueInfo.bind('bean', fetcherReport.urlFetchQueueReport);
      uiURLFetchQueueInfo.setReadOnly(true);
      this.add(uiURLFetchQueueInfo);

      var uiURLFetcherReport = new UIURLFetcherReport();
      uiURLFetcherReport.setBeans(fetcherReport.urlFetcherReport);
      this.add(uiURLFetcherReport);

      var uiURLFetcherMetric = new UIURLFetcherMetric({urlFetcherMetric: fetcherReport.aggregateUrlFetcherMetric });
      this.add(uiURLFetcherMetric);
    },
  }) ;

  return UIFetcherReport ;
});
