define([
  'ui/UITabbedPane',
  'ui/table/UITable',
  'plugins/crawler/site/UISiteStatisticBreadcumbs',
  'plugins/crawler/model',
  'plugins/crawler/Rest'
], function(UITabbedPane, UITable, UISiteStatisticBreadcumbs, model, Rest) {
  var UIURLSchedule = UITable.extend({
    label: "URL Schedule", 

    config: {
      table: { header: "URL Schedule"},
      actions: {
        toolbar: {
          refresh: { label: "Refresh", onClick: function(uiTable) { uiTable.onRefresh(); } }
        },
        bean: {
          more: { label: "More", onClick: function(uiTable, beanState) { } }
        }
      }
    },
    
    onInit: function(options) {
      var urlSchedules = Rest.scheduler.getURLScheduleInfos(100) ;
      this.set(model.scheduler.URLSchedule , urlSchedules);
      this.setTablePageSize(25, false);
    },

    onRefresh: function() {
      var urlSchedules = Rest.scheduler.getURLScheduleInfos(100) ;
      this.setBeans(urlSchedules, true);
    },
  }) ;

  var UIURLCommit = UITable.extend({
    label: "URL Commit", 

    config: {
      table: { header: "URL Commit"},
      actions: {
        toolbar: {
          refresh: { label: "Refresh", onClick: function(uiTable) { uiTable.onRefresh(); } }
        },
        bean: {
          more: { label: "More", onClick: function(uiTable, beanState) { } }
        }
      }
    },
    
    onInit: function(options) {
      var urlCommits = Rest.scheduler.getURLCommitInfos(100) ;
      this.set(model.scheduler.URLCommit , urlCommits);
      this.setTablePageSize(25, false);
    },

    onRefresh: function() {
      var urlCommits = Rest.scheduler.getURLCommitInfos(100) ;
      this.setBeans(urlCommits, true);
    },
  }) ;

  var UICrawlerReport = UITabbedPane.extend({
    label: 'Crawler',

    config: {
      style: "ui-round-tabs",
      tabs: [
        { 
          label: "Site",  name: "site",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UISiteStatisticBreadcumbs()) ;
          }
        },
        { 
          label: "URL Schedule",  name: "urlSchedule",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UIURLSchedule()) ;
          }
        },
        { 
          label: "URL Commit",  name: "urlCommit",
          onSelect: function(thisUI, tabConfig) {
            thisUI.setSelectedTabUIComponent(tabConfig.name, new UIURLCommit()) ;
          }
        }
      ]
    }
  })

  return UICrawlerReport ;
});
