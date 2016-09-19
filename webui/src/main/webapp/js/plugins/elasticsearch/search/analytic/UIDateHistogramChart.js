define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/d3/nv/UINVLinePlusBarChart',
  'plugins/elasticsearch/search/analytic/ChartModel',
], function($, _, Backbone, UINVLinePlusBarChart, chart) {
  var UIDateHistogramChart = UINVLinePlusBarChart.extend({
    label: 'Date Histogram Chart',

    config: {
      width: "100%", height: "600px",
      xAxis: {
        tickFormat: function(value) { return d3.time.format('%x %H:%M:%S')(new Date(value)); }
      },

      x2Axis: {
        tickFormat: function(value) { return d3.time.format('%x %H:%M:%S')(new Date(value)); }
      },

      y1Axis: {
        tickFormat: function(value) { return  value; }
      },

      y2Axis: {
        tickFormat: function(value) { return  d3.format(',f')(value); }
      }
    },
    
    onInit: function(options) {
      this.analyticContext = options.analyticContext;
      this._refresh();
    },
    
    onSearch: function(analyticContext) {
      this.analyticContext = analyticContext;
      this._refresh();
    },
    
    onChangeChartModel: function() {
      this._refresh();
    },
    
    _refresh: function() {
        var esQueryCtx = this.analyticContext.getESQueryContext();
        var chartModel = this.analyticContext.getChartModel();

        var dslQuery = { size:  0, query: esQueryCtx.query, aggs : chartModel.aggs() };

        var result = esQueryCtx.dslQuery(dslQuery);

        var chartDatas = chartModel.buildChartData(result) ;
        this.setData(chartDatas);
      }
  });

  return UIDateHistogramChart ;
});
