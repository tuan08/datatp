define([
  'ui/UIBreadcumbs',
  'ui/table/UITable',
  'plugins/crawler/model',
  'plugins/crawler/Rest'
], function(UIBreadcumbs,UITable, model, Rest) {
  var UISiteStatisticEntry = UITable.extend({
    label: "Site Statistic Detail", 

    config: {
      control: { header: "Control"},
      table: { header: "Site Statistic Detail"},
    },
    
    init: function(statistics) {
      var entries = [];
      for(var name in statistics)  entries.push(statistics[name]);


      var groupByFields = ["category"];
      this.setTableGroupByFields(groupByFields, false);
      this.setTableView('groupby', false);

      this.set(model.site.SiteStatisticEntry, entries);
      return this;
    }
  }) ;

  var UISiteStatistics = UITable.extend({
    label: "Site Statistics", 

    config: {
      control: { header: "Control"},
      table: { header: "Site Statistics"},
      actions: {
        toolbar: {
          refresh: {
            label: "Refresh",
            onClick: function(uiTable) { uiTable.onRefresh(); }
          }
        },

        bean: {
          edit: {
            label: "Detail",
            onClick: function(uiTable, beanState) {
              var siteStatistic = beanState.bean ;
              var uiBreadcumbs = uiTable.getAncestorOfType('UISiteStatisticBreadcumbs');
              uiBreadcumbs.push(new UISiteStatisticEntry().init(siteStatistic.statistics));
            }
          },
        }
      }
    },
    
    onInit: function(options) {
      var siteStatistics = Rest.site.getSiteStatistics() ;
      this.set(model.site.SiteStatistic, siteStatistics);
      this.setTablePageSize(25, false);
    },


    onRefresh: function() {
      var siteStatistics = Rest.site.getSiteStatistics() ;
      this.setBeans(siteStatistics);
      this.refreshTable();
    }
  }) ;

  var UISiteStatisticBreadcumbs = UIBreadcumbs.extend({
    type:  "UISiteStatisticBreadcumbs",

    onInit: function(options) {
      this.uiSiteStatistics = new UISiteStatistics() ;
      this.push(this.uiSiteStatistics);
      return this;
    }
  });

  return UISiteStatisticBreadcumbs;
});
