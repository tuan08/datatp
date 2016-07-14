define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIBean',
  'ui/UITable',
  'plugins/webcrawler/Rest',
], function($, _, Backbone, UIBean, UITable, Rest) {
  var UIURLCommitInfo = UITable.extend({
    label: "URL Commit List",

    config: {
      toolbar: {
        dflt: {
          actions: [
          ]
        }
      },
      
      bean: {
        label: 'URL Commit',
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
            field: "commitURLCount",   label: "Commit URL Count", toggled: true, filterable: true
          },
          { 
            field: "newURLFoundCount",   label: "New URL Found Count", toggled: true, filterable: true
          },
          { 
            field: "newURLTypeList",   label: "New URL Type List", toggled: true, filterable: true
          },
          { 
            field: "newURLTypeDetail",   label: "New URL Type Detail", toggled: true, filterable: true
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
      var urlCommitInfos = Rest.master.getURLCommitInfos(100) ;
      this.setBeans(urlCommitInfos) ;
    }
  });
  
  return new UIURLCommitInfo() ;
});
