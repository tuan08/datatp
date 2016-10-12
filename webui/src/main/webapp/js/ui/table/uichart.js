define([
  'jquery', 'underscore', 'backbone',
  'util/util',
  "ui/UIUtil",
  "ui/UIView",
  'ui/d3/nv3d',
], function($, _, Backbone, util, UIUtil, UIView, nv3d) {
  var tickFormat = {
    number:   d3.format(',.3f'),
    integer:  d3.format(',.0f'),
    datetime: function(value) { return d3.time.format('%x %H:%M:%S')(new Date(value)); },
    date:     function(value) { return d3.time.format('%x')(new Date(value)); },
    time:     function(value) { return d3.time.format('%H:%M:%S')(new Date(value)); },
    raw:     function(value) { return  value ;}
  };

  var configureAxis = function(axis, fieldConfig) {
    if(fieldConfig.datatype != null) axis.datatype = fieldConfig.datatype;
    axis.label.title = fieldConfig.label;
  };

  var opHandler = {
    "set": function(uiTable, obj, property, value) {
      util.reflect.setFieldValue(obj, property, value) ;
    },

    "add": function(uiTable, obj, property, value) {
      util.reflect.addValueToArray(obj, property, value) ;
    },

    "delete": function(uiTable, obj, property, value) {
      util.reflect.deleteField(obj, property) ;
      console.printJSON(obj);
    },

    "setXAxisMapField": function(uiTable, obj,  property, value) {
      obj.xAxis.mapField = value  ;
      var fieldConfig = uiTable.beanInfo.fields[value];
      configureAxis(obj.xAxis, fieldConfig);
      console.printJSON(obj.xAxis);
    },

    "addYAxisMapField": function(uiTable, obj,  property, value) {
      var field = value;
      obj.yAxis.mapFields[field] = true ;
      var fieldConfig = uiTable.beanInfo.fields[value];
      configureAxis(obj.yAxis, fieldConfig);
      console.printJSON(obj.yAxis);
    }
  }

  var UINVChart = UIView.extend({

    defaultConfig: { 
      type: "BarChart",
      width:  "100%", height: "600px", 
      xAxis: {
        label: { title: "X Axis", rotate: 0 },
        tickFormat: "time",
        mapField: null, datatype: "number" 
      },

      yAxis: {
        label: { title: "Y Axis" },
        tickFormat: "number",
        datatype: "number", mapFields: { }
      }
    },

    initialize: function (options) {
      this.data =  [];
      this.__init(options);
      this.config.id = "ui-chart-" + UIUtil.guid();
      if(this.onInit) this.onInit(options);
    },

    _template: _.template(`
      <div id='<%=config.id%>' style='width: <%=config.width%>; height: <%=config.height%>'><svg></svg></div>
    `),

    render: function() {
      this.configure(this.chart, this.config);
      var params = { config: this.config };
      $(this.el).html(this._template(params));
      d3.select('#' + this.config.id + ' svg').datum(this.data).call(this.chart);
      nv.utils.windowResize(this.chart.update);
      nv.addGraph(function() { return this.chart; });
    },

    setData: function (data) { this.data = data ; },

    clearChartData: function () { this.data = []; },

    /** chart data should in format { key: "name", values: [ {x: xValue, y: yValue}]*/
    addChartData: function (chartData) { this.data.push(chartData) ; },

    firePropertyChange: function(object, op, property, value) {
      opHandler[op](this.uiTable, this.config, property, value);
      this.updateChartData();
    },

    tableCreateChartData: function(uiTable, name, xField, yField) {
      var beanStates = uiTable.filterBeanStates;
      var xyCoords = [];
      for(var i = 0; i < beanStates.length; i++) {
        var bean = beanStates[i].bean;
        var x = util.reflect.getFieldValue(bean, xField);
        var y = util.reflect.getFieldValue(bean, yField);
        xyCoords.push({x: x, y: y});
      }
      var chartData = { key: name, values: xyCoords };
      return chartData;
    }
  });

  var UITableBarChart = UINVChart.extend({
    chartType: "BarChart",

    __init: function(options) {
      this.config = this.mergeConfig();

      var chart = nv.models.multiBarChart();
      chart.
        duration(150).groupSpacing(0.1).
        margin({ bottom: 75, left: 75, right: 50 }).
        reduceXTicks(true).staggerLabels(false);
      //chart.barColor(d3.scale.category20().range()).

      chart.xAxis.axisLabelDistance(30).showMaxMin(false);

      chart.yAxis.axisLabelDistance(0) ;

      chart.dispatch.on('renderEnd', function() { nv.log('Render Complete'); });
      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });
      chart.state.dispatch.on('change', function(state) { nv.log('state', JSON.stringify(state)); });
      this.chart = chart;
    },

    configure: function(chart, config) { 
      chart.
        rotateLabels(config.xAxis.label.rotate);

      chart.xAxis.
        axisLabel(config.xAxis.label.title).
        tickFormat(tickFormat[config.xAxis.tickFormat]) ;

      chart.yAxis.
        axisLabel(config.yAxis.label.title).
        tickFormat(tickFormat[config.yAxis.tickFormat]) ;
    },


    init: function(uiTable) {
      this.uiTable = uiTable;
      uiTable.config.table.chart = this.config;
    },


    updateChartData: function() {
      this.clearChartData();
      var chart = this.config;
      if(chart.xAxis.mapField == null) return;
      var yAxisFieldCount = Object.keys(chart.yAxis.mapFields).length;
      if(yAxisFieldCount == 0) return;
      var beanStates = this.uiTable.filterBeanStates;
      for(var fName in chart.yAxis.mapFields) {
        var chartData = this.tableCreateChartData(this.uiTable, fName, chart.xAxis.mapField, fName);
        this.addChartData(chartData);
      }
    }
  });

  var UITableLinePlusBarChart = UINVChart.extend({
    chartType: "LinePlusBarChart",

    overrideConfig: {
      type: "LinePlusBarChart",
      xAxis: { label: { title: "X1 Axis" } },
      yAxis: { label: { title: "Y1 Axis" } }
    },

    __init: function(options) {
      this.config = this.mergeConfig(this.overrideConfig);

      var chart = nv.models.linePlusBarChart();
      chart.
        margin({top: 50, right: 80, bottom: 30, left: 80}).
        legendLeftAxisHint('').
        legendRightAxisHint('*').
        color(d3.scale.category10().range());

      chart.bars.forceY([0]).padData(false);


      chart.xAxis.
        showMaxMin(false).
        staggerLabels(false).
        tickFormat(tickFormat[this.config.xAxis.tickFormat]).
        rotateLabels(this.config.xAxis.label.rotate).
        axisLabel(this.config.xAxis.label.title);

      chart.x2Axis.
        showMaxMin(false).
        staggerLabels(false).
        tickFormat(tickFormat[this.config.xAxis.tickFormat]).
        rotateLabels(this.config.xAxis.label.rotate).
        axisLabel(this.config.xAxis.label.title);

      chart.y1Axis.
        showMaxMin(true).
        axisLabelDistance(0).
        axisLabel(this.config.yAxis.label.title).
        tickFormat(tickFormat[this.config.yAxis.tickFormat]) ;

      chart.y2Axis.
        tickFormat(tickFormat['raw']) ;

      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });
      this.chart = chart;
    },

    configure: function(chart, config) { 
    },

    init: function(uiTable) {
      this.uiTable = uiTable;
      uiTable.config.table.chart = this.config;
    },

    updateChartData: function() {
      this.clearChartData();
      var chart = this.uiTable.config.table.chart;
      if(chart.xAxis.mapField == null) return;
      var yAxisFieldCount = Object.keys(chart.yAxis.mapFields).length;
      if(yAxisFieldCount == 0) return;
      for(var fName in chart.yAxis.mapFields) {
        var chartData = this.tableCreateChartData(this.uiTable, fName, chart.xAxis.mapField, fName);
        chartData.bar = true;
        this.addChartData(chartData);
      }
    }
  });

  var uichart = {
    UITableBarChart: UITableBarChart,
    UITableLinePlusBarChart: UITableLinePlusBarChart
  }

  return uichart;
});
