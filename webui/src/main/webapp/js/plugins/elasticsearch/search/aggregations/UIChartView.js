define([
  'jquery', 
  'underscore', 
  'ui/UIView',
  'ui/nvd3'
], function($, _, UIView, nvd3) {

  var UILinePlusBarChart = nvd3.UILinePlusBarChart.extend({
    config: {
      width: "100%", height: "600px",
      xAxis: { 
        label: { title: "X1 Axis" },
        tickFormat: "datetime" 
      },

      yAxis: { 
        label: { title: "Y1 Axis" },
        tickFormat: "integer" 
      },

      y2Axis: { 
        label: { title: "Y2 Axis" },
        tickFormat: "integer" 
      },
    },
  });

  var UIBarChart = nvd3.UIBarChart.extend({
    
    config: {
      xAxis: {
        label: { title: "X Axis" },
        tickFormat: "datetime"
      },

      yAxis: {
        label: { title: "Y Axis" },
        tickFormat: "integer",
      }
    }
  });

  var UIChartView = UIView.extend({

    initialize: function (options) {
    },

    _template: _.template(`
      <div class="ui-card">
        <h6>Hello Chart</h6>
        <div class="ui-card-content">Chart</div>
      </div>
    `),

    render: function() {
      var params = { };
      $(this.el).html(this._template(params));
      if(this.uiChart != null) {
        var uiContent = this.$('.ui-card-content') ;
        uiContent.unbind() ;
        this.uiChart.setElement(uiContent).render();
      }
    },

    events: {
      'click a.onSetPageSize100': 'onSetPageSize100'
    },

    onUpdateResult: function(aggContext) {
      this.aggContext = aggContext;
      var data = aggContext.getAggregations().buildChartDataModel(aggContext.getResult());
      this.uiChart = new UIBarChart();
      this.uiChart.setData(data);
    },

    selectChartType: function(type) {
      if(type == 'LinePlusBarChart') {
        this.uiChart = new UILinePlusBarChart();
      } else {
        this.uiChart = new UIBarChart();
      }
      var data = this.aggContext.getAggregations().buildChartDataModel(this.aggContext.getResult());
      this.uiChart.setData(data);
      this.render();
    },
  });

  return UIChartView ;
});
