define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/analytic/UIDateHistogramChart',
  'plugins/elasticsearch/search/analytic/ChartModel'
], function($, _, Backbone, UIDateHistogramChart, chart) {

  var UIAnalyticWS = Backbone.View.extend({
    label: 'Analytic WS',
    
    initialize: function(options) {
      this.analyticContext = options.analyticContext;
      
      var esQueryCtx = this.analyticContext.getESQueryContext();
      var chartModel = new chart.model.DateHistogramModel('document', 'timestamp', '1m') ;
      chartModel.addSubAggregation(new chart.aggregation.TermsAggregation('Content Type', 'entity.content.type', 10));
      chartModel.addSubAggregation(new chart.aggregation.TermsAggregation('Page Type', 'attr.pageType', 10));
      this.analyticContext.setChartModel(chartModel);
      
      this.uiChart = new UIDateHistogramChart({analyticContext: this.analyticContext});
    },
      
    _template: _.template(`
      <div style='padding: 10px 5px'> 
        <div class='UIChart'></div>
      </div>
    `),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
      this.uiChart.setElement($(this.el).find('.UIChart')).render();
    },

    events: {
      "click     .onToggleField": "onToggleField",
    },

    onChangeChartModel: function() {
      this.uiChart.onChangeChartModel();
    },
    
    onSearch: function(analyticContext) {
      this.uiChart.onSearch(analyticContext);
    }
  });
  
  return UIAnalyticWS ;
});
