define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITable',
  'ui/UIBean',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITable, UIBean, Rest) {
  var UIURLSchedulerStatus = UIBean.extend({
    label: "URL Scheduler Status",
    config: {
      beans: {
        bean: {
          name: 'status', label: 'URL Scheduler Status',
          fields: [
            { field: "status",   label: "Status" },
          ],
          edit: {
            actions: [ ],
          },
          view: {
            actions: [ ]
          }
        }
      }
    }
  });

  var UIFetcherStatus = UITable.extend({
    label: "Fetcher Status",

    config: {
      toolbar: {
        dflt: {
          actions: [ ]
        }
      },
      
      bean: {
        label: 'Fetcher Status',
        fields: [
          { field:  "id", label: "Id",  toggled: true, filterable: true },
          { field:  "host", label: "Host",  toggled: true, filterable: true },
          { field:  "status", label: "Status",  toggled: true, filterable: true }
        ],
        actions:[ ]
      }
    },

    onInit: function(options) {
      var fetcherStatus = options.fetcherStatus ;
      this.setBeans(fetcherStatus) ;
    }
  });

  var UICrawlerStatus = UICollabsible.extend({
    label: "Crawler Status", 
    config: {
      actions: [
        { 
          action: "start", label: "Start",
          onClick: function(thisUI) {
            var crawlerStatus = Rest.crawler.start();
            thisUI.onRefresh(crawlerStatus);
          }
        },
        { 
          action: "stop", label: "Stop",
          onClick: function(thisUI) {
            var crawlerStatus = Rest.crawler.stop();
            thisUI.onRefresh(crawlerStatus);
          }
        },
        { 
          action: "refresh", label: "Refresh",
          onClick: function(thisUI) {
            var crawlerStatus = Rest.crawler.getStatus();
            thisUI.onRefresh(crawlerStatus);
          }
        }
      ]
    },

    onInit: function(options) {
      var crawlerStatus = Rest.crawler.getStatus();
      this.onRefresh(crawlerStatus);;
    },

    onRefresh: function(crawlerStatus) {
      this.clear();
      
      var urlSchedulerStatus = { 
        status: crawlerStatus.urlSchedulerStatus 
      } ;

      var uiURLSchedulerStatus = new UIURLSchedulerStatus();
      uiURLSchedulerStatus.bind('bean', urlSchedulerStatus);
      uiURLSchedulerStatus.setReadOnly(true);
      this.add(uiURLSchedulerStatus);

      var uiFetcherStatus = new UIFetcherStatus({fetcherStatus: crawlerStatus.fetcherStatus});
      this.add(uiFetcherStatus);
      this.render();
    }
  }) ;

  return new UICrawlerStatus() ;
});
