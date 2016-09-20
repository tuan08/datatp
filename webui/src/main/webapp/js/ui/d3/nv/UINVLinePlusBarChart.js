define([
  'jquery', 
  'underscore', 
  'backbone',
  "ui/d3/nv/UINVChart"
], function($, _, Backbone, UINVChart) {

  var UINVLinePlusBarChart = UINVChart.extend({
    setData: function (data) { this.data = data ; },

    createChart: function(config, chartData) {
      var chart = nv.models.linePlusBarChart();
      chart.
        margin({top: 50, right: 80, bottom: 30, left: 80}).
        legendLeftAxisHint('').
        legendRightAxisHint('*').
        color(d3.scale.category10().range());


      chart.xAxis.tickFormat(function(d) { return config.xAxis.tickFormat(d); }).showMaxMin(false);
      //chart.xAxis.rotateLabels(-7.5);

      chart.x2Axis.tickFormat(function(d) { return config.x2Axis.tickFormat(d); }).showMaxMin(false);
      //chart.x2Axis.rotateLabels(-7.5);

      chart.y1Axis.tickFormat(function(d) { return config.y1Axis.tickFormat(d); });
      chart.y2Axis.tickFormat(function(d) { return config.y2Axis.tickFormat(d); });

      chart.bars.forceY([0]).padData(false);


      d3.select('#' + config.id + ' svg').datum(chartData).transition().duration(500).call(chart);

      nv.utils.windowResize(chart.update);

      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });

      return chart;
    }
  });
  
  return UINVLinePlusBarChart ;
});
