define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UICollapsible',
  'ui/UITableTree',
  'plugins/crawler/Rest'
], function($, _, Backbone, UICollabsible, UITableTree,  Rest) {

  var UIURLStatistics = UITableTree.extend({
    label: "URL Statistics",

    config: {
      bean: {
        label: 'URL Statistics',
        fields: [
          { 
            field: "category", label: "Category", defaultValue: '', toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
            }
          },
          { field: "name", label: "Name", toggled: true },
          { field: "frequency", label: "Frequency", toggled: true }
        ]
      }
    },

    onInit: function(options) {
      var siteStatistic   = options.siteStatistic;
      var urlStatistics   = siteStatistic.urlStatistics.statistics;
      console.printJSON(urlStatistics);
      for (var name in urlStatistics) {
        this._addCategory(name, urlStatistics[name]);
      }
    },
    
    _addCategory: function(categoryName, category) {
      var categoryNode = this.addNode({category: categoryName});
      categoryNode.setDisableAction(true);
      categoryNode.setCollapse(false);

      for (var name in category) {
        var code =  categoryNode.addChild(
          { category: "", name: name, frequency: category[name].frequency }
        );
      }
    }
  });
  
  var UISiteStatisticDetail = UICollabsible.extend({
    label: "Site Statistic Detail", 
    config: {
      actions: [
        {
          action: "refresh", label: "Refresh",
          onClick: function(thisUI) { 
          }
        }
      ]
    },

    onInit: function(options) {
      this.siteStatistic = options.siteStatistic;
      var options = { siteStatistic: this.siteStatistic};
      this.add(new UIURLStatistics(options));
    }
  }) ;

  return UISiteStatisticDetail ;
});
