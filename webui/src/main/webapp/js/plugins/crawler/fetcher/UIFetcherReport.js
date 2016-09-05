define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITable',
  'ui/UIBean',
  'ui/UIBreadcumbs',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITable, UIBean, UIBreadcumbs, Rest) {
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
              getDisplay: function(bean) { return bean.metric.fetchCount; }
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
      ]
    },

    onInit: function(options) {
      var fetcherReport = options.fetcherReport;
       
      var uiFetcherStatus = new UIFetcherStatus();
      uiFetcherStatus.bind('bean', fetcherReport.status);
      uiFetcherStatus.setReadOnly(true);
      this.add(uiFetcherStatus);

      var uiURLFetcherReport = new UIURLFetcherReport();
      uiURLFetcherReport.setBeans(fetcherReport.urlFetcherReport);
      this.add(uiURLFetcherReport);
    }
  }) ;

  return UIFetcherReport ;
});
