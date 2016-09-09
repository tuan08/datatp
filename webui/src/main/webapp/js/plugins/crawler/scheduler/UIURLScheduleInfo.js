define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'plugins/crawler/Rest',
], function($, _, Backbone, UIBean, UITable, Rest) {
  var UIURLScheduleInfo = UITable.extend({
    label: "URL Schedule List",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              icon: "refresh", label: "Refresh",
              onClick: function(thisTable) { thisTable.refresh() ; } 
            }
          ]
        }
      },
      
      bean: {
        label: 'URL Schedule',
        fields: [
          { 
            field: "time",   label: "Time", toggled: true, filterable: true,
            custom: {
              getDisplay: function(bean) { return new Date(bean.time); }
            },
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(bean)) ;
            }
          },
          { field: "execTime",   label: "Exec Time", toggled: true, filterable: true },
          { field: "urlCount",   label: "URL", toggled: true, filterable: true },
          { field: "urlListCount",   label: "URL List", toggled: true, filterable: true },
          { field: "urlDetailCount",   label: "URL Detail", toggled: true, filterable: true },
          { field: "urlUncategorizedCount",   label: "URL Uncategorized", toggled: true, filterable: true },
          { field: "scheduleCount",   label: "Schedule", toggled: true, filterable: true },
          { field: "delayScheduleCount",   label: "Delay Schedule", toggled: true, filterable: true },
          { field: "pendingCount",   label: "Pending", toggled: true, filterable: true },
          { field: "expiredPendingCount",   label: "Expired Pending", toggled: true, filterable: true },
          { field: "waitingCount",   label: "Waiting", toggled: true, filterable: true },
        ]
      }
    },

    onInit: function(options) { 
      this.setBeans(Rest.scheduler.getURLScheduleInfos(100)) ;
    },

    refresh: function() {
      this.setBeans(Rest.scheduler.getURLScheduleInfos(100)) ;
      this.render();
    }
  });
  
  return UIURLScheduleInfo ;
});
