define([
  'jquery', 
  'underscore', 
  'backbone',
  "nv",
  "ui/UIUtil",
  "css!../../libs/d3/nv/1.8.4-dev/nv.d3.min.css"
], function($, _, Backbone, nv, UIUtil) {

  var tickFormat = {
    number:   d3.format(',.3f'),
    integer:  d3.format(',.0f'),
    datetime: function(value) { return d3.time.format('%x %H:%M:%S')(new Date(value)); },
    date:     function(value) { return d3.time.format('%x')(new Date(value)); },
    time:     function(value) { return d3.time.format('%H:%M:%S')(new Date(value)); },
    raw:     function(value) { return  value ;}
  };

  var UINVChart = Backbone.View.extend({

    initialize: function (options) {
      this.data =  [];
      this.__init(options);
      if(this.onInit) this.onInit(options);
    },

    _template: _.template(`
      <div id='<%=config.id%>' style='width: <%=config.width%>; height: <%=config.height%>'><svg></svg></div>
    `),

    render: function() {
      var params = { config: this.config };
      $(this.el).html(this._template(params));
      var chart = this.createChart(this.config, this.data) ;
      nv.addGraph(function() { return chart; });
    },

    setData: function (data) { this.data = data ; },


    clearChartData: function () { this.data = []; },

    createChart: function() { throw new Error('This method should be overrided'); }
  });


  var UINVBarChart = UINVChart.extend({
    __init: function(options) {

      var defaultConfig = { 
        id: "ui-chart-" + UIUtil.guid(),
        width:  "100%", height: "600px", 
        xAxis: {
          label: { title: "X Axis", rotate: 25 },
          tickFormat: "time"
        },
        yAxis: {
          label: { title: "Y Axis" },
          tickFormat: tickFormat.integer
        }
      }
      if(this.config) $.extend(true, defaultConfig, this.config);
      this.config = defaultConfig;
    },

    addChartData: function (name, xyCoordinates) { 
      var chartData = { key: name, values: xyCoordinates };
      this.data.push(chartData) ; 
    },

    createChart: function(config, chartData) {
      var chart = nv.models.multiBarChart();
      chart.
        //barColor(d3.scale.category20().range()).
        duration(150).
        margin({ bottom: 75, left: 75, right: 50 }).
        rotateLabels(this.config.xAxis.label.rotate).
        groupSpacing(0.1).
        reduceXTicks(true).
        staggerLabels(false);

      chart.xAxis.
        axisLabel(this.config.xAxis.label.title).
        axisLabelDistance(30).
        showMaxMin(false).
        tickFormat(tickFormat[this.config.xAxis.tickFormat]) ;

      chart.yAxis.
        axisLabel(this.config.yAxis.label.title).
        axisLabelDistance(0).
        tickFormat(this.config.yAxis.tickFormat) ;

      chart.dispatch.on('renderEnd', function() { nv.log('Render Complete'); });

      d3.select('#' + config.id + ' svg').datum(chartData).call(chart);

      nv.utils.windowResize(chart.update);

      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });

      chart.state.dispatch.on('change', function(state) { nv.log('state', JSON.stringify(state)); });

      return chart;
    }
  });

  var UINVLinePlusBarChart = UINVChart.extend({
    __init: function(options) {
      var defaultConfig = { 
        id: "ui-chart-" + UIUtil.guid(),
        width:  "100%", height: "500px", 
      }
      if(this.config) $.extend(true, defaultConfig, this.config);
      this.config = defaultConfig;
    },

    addChartData: function (name, xyCoordinates, bar) { 
      var chartData = { key: name, values: xyCoordinates, bar: bar };
      this.data.push(chartData) ; 
    },

    createChart: function(config, chartData) {
      var chart = nv.models.linePlusBarChart();
      chart.
        margin({top: 50, right: 80, bottom: 30, left: 80}).
        legendLeftAxisHint('').
        legendRightAxisHint('*').
        color(d3.scale.category10().range());

      chart.bars.forceY([0]).padData(false);


      chart.xAxis.
        tickFormat(function(d) { return config.xAxis.tickFormat(d); }).
        showMaxMin(false).
        rotateLabels(6.5).
        axisLabel("X Axis");

      chart.x2Axis.
        tickFormat(function(d) { return config.x2Axis.tickFormat(d); }).
        showMaxMin(false);
      //chart.x2Axis.staggerLabels(true);
      //chart.x2Axis.rotateLabels(7.5);

      chart.y1Axis.
        tickFormat(function(d) { return config.y1Axis.tickFormat(d); }).
        showMaxMin(true).
        axisLabel("Y Axis").
        axisLabelDistance(0);

      chart.y2Axis.
        tickFormat(function(d) { return config.y2Axis.tickFormat(d); });

      d3.select('#' + config.id + ' svg').datum(chartData).transition().duration(500).call(chart);

      nv.utils.windowResize(chart.update);

      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });

      return chart;
    }
  });
  
  var UINVMultiChart = UINVChart.extend({
    __init: function(options) {
      var defaultConfig = { 
        id: "ui-chart-" + UIUtil.guid(),
        width:  "100%", height: "500px", 
      }
      if(this.config) $.extend(true, defaultConfig, this.config);
      this.config = defaultConfig;
    },

    addChartData: function (name, xyCoordinates) { 
      var chartData = { key: name, values: xyCoordinates };
      this.data.push(chartData) ; 
    },

    createChart: function(config, chartData) {
      var chart = nv.models.multiChart().margin({top: 30, right: 60, bottom: 50, left: 70}).color(d3.scale.category10().range());

      chart.xAxis.tickFormat(d3.format(',f'));
      chart.yAxis1.tickFormat(d3.format(',.1f'));
      chart.yAxis2.tickFormat(d3.format(',.1f'));

      d3.select('#' + config.id + ' svg').datum(chartData).transition().duration(500).call(chart);

      return chart;
    }
  });

  var nv3d = {
    ui: { 
      UINVChart:    UINVChart,
      UINVBarChart: UINVBarChart,
      UINVLinePlusBarChart: UINVLinePlusBarChart,
      UINVMultiChart: UINVMultiChart
    },
    tickFormat: tickFormat
  }

  return nv3d ;
});
