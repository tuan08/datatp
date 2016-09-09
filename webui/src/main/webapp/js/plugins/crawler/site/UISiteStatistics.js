define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UITabbedPane',
  'ui/UITable',
  'plugins/crawler/site/UISiteStatisticDetail',
  'plugins/crawler/Rest',
], function($, _, Backbone, UITabbedPane, UITable, UISiteStatisticDetail, Rest) {

  var UISiteStatisticList = UITable.extend({
    label: "Site Statistics",

    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "refresh", label: "Refresh", 
              onClick: function(thisTable) { 
                var siteStatistics = Rest.site.getSiteStatistics() ;
                thisTable.setBeans(siteStatistics) ;
                thisTable.render();
              } 
            },
          ]
        }
      },
      
      bean: {
        label: 'Site Statistic',
        fields: [
          { 
            field: "hostname",   label: "Hostname", toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var siteStatistic = thisTable.getItemOnCurrentPage(row) ;
              var uiSiteStatistics = thisTable.getAncestorOfType('UISiteStatistics');
              uiSiteStatistics.addSiteStatisticTab(siteStatistic);
            }
          },
          { field: "group",   label: "Group", toggled: true, filterable: true },
          { field: "scheduleCount",   label: "Schedule", toggled: true, filterable: true },
          { field: "commitCount",   label: "commit", toggled: true, filterable: true },
          { field: "inQueue",   label: "In Queue", toggled: true, filterable: true }
        ]
      }
    },

    onInit: function(options) {
      var siteStatistics = Rest.site.getSiteStatistics() ;
      this.setBeans(siteStatistics) ;
    }
  });

  var UISiteStatistics = UITabbedPane.extend({
    type: 'UISiteStatistics',
    label: 'Site Configs',

    config: {
      tabs: [ ]
    },
    
    onInit: function(options) {
      this.addTab("siteStatisticList", "Site Statistics", new UISiteStatisticList(), false, true);
    },
    
    addSiteStatisticTab: function(siteStatistic) {
      var name = siteStatistic.hostname;
      this.addTab(name, name, new UISiteStatisticDetail({ siteStatistic: siteStatistic }), true, true);
      this.render();
    }
  });

  return new UISiteStatistics() ;
  
});
