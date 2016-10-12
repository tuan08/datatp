define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/bean/UIBean',
  'ui/table/UITable',
  'plugins/uidemo/bean/data'
], function($, _, Backbone, UIBean, UITable, data) {

  var UITableBarChartDemo = UITable.extend({
    label: "UITable Bar Chart Demo", 

    config: {
      control: { header: "Table Control"},
      table: { header: "Table Bar Chart Demo"},
      actions: {
        toolbar: {
          refresh: {
            label: "Refresh",
            onClick: function(uiTable) { 
              uiTable.setBeans(data.chart.BarChart.create(100), true);
            }
          }
        }
      }
    },
    
    onInit: function(options) {
      this.addDefaultControlPluginUI();
      this.set(data.chart.BarChart.Model, data.chart.BarChart.create(100));
    }
  }) ;

  return new UITableBarChartDemo() ;
});
