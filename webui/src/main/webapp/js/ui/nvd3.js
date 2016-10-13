define([
  'jquery', 'underscore', 'backbone', "d3", "nv",
  'util/util',
  "ui/UIUtil",
  "ui/UIView",
  "css!../libs/d3/nv/1.8.4-dev/nv.d3.min.css"
], function($, _, Backbone, d3, nv, util, UIUtil, UIView) {
  var tickFormatFunctions = {
    number:   d3.format(',.2f'),
    integer:  d3.format(',.0f'),
    datetime: function(value) { return d3.time.format('%x %H:%M')(new Date(value)); },
    date:     function(value) { return d3.time.format('%x')(new Date(value)); },
    time:     function(value) { return d3.time.format('%H:%M:%S')(new Date(value)); },
    raw:     function(value) { return  value ;}
  };

  var configureAxisWithFieldConfig = function(axisConfig, config, mapField) {
    if(!config.beanModel) return;
    var fieldConfig = config.beanModel.fields[mapField];
    if(fieldConfig.datatype != null) axisConfig.datatype = fieldConfig.datatype;
  };

  var opConfigHandler = {
    "set": function(config, property, value) {
      util.reflect.setFieldValue(config, property, value) ;
    },

    "add": function(config, property, value) {
      util.reflect.addValueToArray(config, property, value) ;
    },

    "delete": function(config, property, value) {
      util.reflect.deleteField(config, property) ;
    },

    "setAxisMapField": function(config,  property, value) {
      var field = value;
      var axis = util.reflect.getFieldValue(config, property);
      axis.mapField = field  ;
      configureAxisWithFieldConfig(axis, config, field);
    },

    "addAxisMapField": function(config,  property, value) {
      var field = value;
      var axis = util.reflect.getFieldValue(config, property);
      axis.mapFields[field] = true; 
      configureAxisWithFieldConfig(axis, config, field);
    },

    "deleteAxisMapField": function(config,  property, value) {
      var field = value;
      var axis = util.reflect.getFieldValue(config, property);
      delete axis.mapFields[field] ; 
    }
  }

  var UINVChart = UIView.extend({

    defaultConfig: { 
      type: "BarChart",
      width:  "100%", height: "600px", 
      xAxis: {
        label: { title: "X Axis", rotate: 0 },
        tickFormat: "auto",
        mapField: null, datatype: "number" 
      },

      yAxis: {
        label: { title: "Y Axis" },
        tickFormat: "auto",
        datatype: "number", mapFields: { }
      }
    },

    initialize: function (options) {
      this.data =  [];
      this.__init(options);
      this.config.id = "nv3d-chart-" + UIUtil.guid();
      if(this.onInit) this.onInit(options);
    },

    _template: _.template(`
      <div id="<%=config.id%>" style="width: <%=config.width%>; height: <%=config.height%>">
        <svg></svg>
      </div>
    `),

    render: function() {
      var config = this.config;
      var params = { config: config };
      $(this.el).html(this._template(params));

      var chart = this.chart;
      this.configure(chart, this.config);
      d3.select('#' + config.id + ' svg').datum(this.data).call(chart);

      nv.utils.windowResize(chart.update);
      nv.addGraph(function() { return chart; });
    },

    useDataFromUITable: function(uiTable) {
      this.uiTable = uiTable;
      this.config.beanModel = uiTable.beanInfo;
      uiTable.config.table.chart = this.config;
      return this;
    },

    setData: function (data) { this.data = data ; },

    clearChartData: function () { this.data = []; },

    /** chart data should in format { key: "name", values: [ {x: xValue, y: yValue}]*/
    addChartData: function (chartData) { this.data.push(chartData) ; },

    configureAxis: function(axis, axisConfig) {
      var tickFormat = axisConfig.tickFormat;
      if(tickFormat == null || typeof tickFormat === 'string' || tickFormat instanceof String) {
        if(tickFormat == null || tickFormat == "" || tickFormat == "auto") {
          tickFormat = axisConfig.datatype;
        }
        if(tickFormat == null) tickFormat = "raw";
        tickFormat = tickFormatFunctions[tickFormat] ;
      }
      axis.
        axisLabel(axisConfig.label.title).
        tickFormat(tickFormat) ;
    },

    firePropertyChange: function(object, op, property, value) {
      opConfigHandler[op](this.config, property, value);
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

  var UIBarChart = UINVChart.extend({
    chartType: "BarChart",

    __init: function(options) {
      this.config = this.mergeConfig();
      this.chart = this.createChart();
    },

    createChart: function() {
      var chart = nv.models.multiBarChart();
      chart.
        duration(300).groupSpacing(0.1).stacked(false).
        margin({ bottom: 75, left: 75, right: 50 }).
        reduceXTicks(true).staggerLabels(false);
      //chart.barColor(d3.scale.category20().range()).
      
      chart.xAxis.axisLabelDistance(30).showMaxMin(false);

      chart.yAxis.axisLabelDistance(0) ;

      chart.dispatch.on('renderEnd', function() { nv.log('Render Complete'); });
      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });
      chart.state.dispatch.on('change', function(state) { nv.log('state', JSON.stringify(state)); });
      return chart;
    },


    configure: function(chart, config) { 
      chart.rotateLabels(config.xAxis.label.rotate);
      this.configureAxis(chart.xAxis, config.xAxis);
      this.configureAxis(chart.yAxis, config.yAxis);
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

  var UILinePlusBarChart = UINVChart.extend({
    chartType: "LinePlusBarChart",

    overrideConfig: {
      type: "LinePlusBarChart",
      xAxis: { label: { title: "X1 Axis" } },
      yAxis: { label: { title: "Y1 Axis" } },

      y2Axis: {
        label: { title: "Y2 Axis" },
        tickFormat: "auto",
        datatype: "number", mapFields: { }
      }
    },

    __init: function(options) {
      this.config = this.mergeConfig(null);
      this.chart = this.createChart();
    },

    createChart: function() {
      var chart = nv.models.linePlusBarChart();
      chart.
        margin({top: 50, right: 80, bottom: 30, left: 80}).
        legendLeftAxisHint('').
        legendRightAxisHint('*').
        color(d3.scale.category10().range());

      chart.bars.forceY([0]).padData(false);

      chart.xAxis.showMaxMin(false).staggerLabels(false).rotateLabels(this.config.xAxis.label.rotate);

      chart.x2Axis.showMaxMin(false).staggerLabels(false).rotateLabels(this.config.xAxis.label.rotate);

      chart.y1Axis.showMaxMin(true).axisLabelDistance(0);

      chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });
      return chart;
    },

    configure: function(chart, config) { 
      this.configureAxis(chart.xAxis, config.xAxis);
      this.configureAxis(chart.x2Axis, config.xAxis);
      this.configureAxis(chart.y1Axis, config.yAxis);
      this.configureAxis(chart.y2Axis, config.y2Axis);
    },

    updateChartData: function() {
      this.clearChartData();
      var chart = this.uiTable.config.table.chart;
      if(chart.xAxis.mapField == null) return;
      var yAxisFieldCount = Object.keys(chart.yAxis.mapFields).length;
      var y2AxisFieldCount = Object.keys(chart.y2Axis.mapFields).length;
      if(yAxisFieldCount == 0 && y2AxisFieldCount == 0) return;
      for(var fName in chart.yAxis.mapFields) {
        var chartData = this.tableCreateChartData(this.uiTable, fName, chart.xAxis.mapField, fName);
        chartData.bar = true;
        this.addChartData(chartData);
      }

      for(var fName in chart.y2Axis.mapFields) {
        var chartData = this.tableCreateChartData(this.uiTable, fName, chart.xAxis.mapField, fName);
        this.addChartData(chartData);
      }
    }
  });

  var UIMultiChart = UINVChart.extend({
    __init: function(options) {
      this.config = this.mergeConfig(null);

      var chart = nv.models.multiChart();
      chart.margin({top: 30, right: 60, bottom: 50, left: 70}).color(d3.scale.category10().range());
      this.chart = chart;
    },

    configure: function(chart, config) { 
      chart.xAxis.tickFormat(d3.format(',f'));
      chart.yAxis1.tickFormat(d3.format(',.1f'));
      chart.yAxis2.tickFormat(d3.format(',.1f'));
    },
  });

  var nvd3 = {
    UIBarChart: UIBarChart,
    UILinePlusBarChart: UILinePlusBarChart,
    UIMultiChart: UIMultiChart
  }

  return nvd3;
});
