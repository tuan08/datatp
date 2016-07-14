define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'plugins/webcrawler/Rest',
], function($, _, Backbone, UIBean, UITable, Rest) {
  var UIURLScheduleInfo = UITable.extend({
    label: "URL Schedule List",

    config: {
      toolbar: {
        dflt: {
          actions: [
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
          { 
            field: "execTime",   label: "Exec Time", toggled: true, filterable: true
          },
          { 
            field: "urlCount",   label: "URL Count", toggled: true, filterable: true
          },
          { 
            field: "sheduleCount",   label: "Schedule Count", toggled: true, filterable: true
          },
          { 
            field: "delaySheduleCount",   label: "Delay Schedule Count", toggled: true, filterable: true
          },
          { 
            field: "pendingCount",   label: "Pendding Count", toggled: true, filterable: true
          },
          { 
            field: "waittingCount",   label: "Waitting Count", toggled: true, filterable: true
          },
        ],
        actions:[
          {
            icon: "delete", label: "Delete",
            onClick: function(thisTable, row) { 
              thisTable.markDeletedItemOnCurrentPage(row) ;
              console.log('Mark delete row ' + row);
            }
          }
        ]
      }
    },

    onInit: function(options) {
      var urlCommitInfos = Rest.master.getURLScheduleInfos(100) ;
      this.setBeans(urlCommitInfos) ;
    }
  });
  
  return new UIURLScheduleInfo() ;
});
