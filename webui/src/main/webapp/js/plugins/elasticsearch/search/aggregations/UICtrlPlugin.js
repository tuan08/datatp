define([
  'jquery', 
  'underscore', 
  'ui/widget', 
  'ui/UIView', 
  'text!plugins/elasticsearch/search/aggregations/UICtrlPlugin.jtpl',
], function($, _, widget, UIView, Template) {

  var UICtrlPlugin = UIView.extend({
    initialize: function (options) {
    },

    _template: _.template(Template),

    render: function() {
      var params = { 
        aggregations:   this.aggContext.getAggregations(),
        searchHitModel: this.aggContext.getESQueryContext().getSearchHitModel(),
        widget: widget
      };
      $(this.el).html(this._template(params));
    },
    
    withContext: function(aggContext) {
      this.aggContext = aggContext;
      return this;
    },


    events: {
      'click  .onSelectChartType': 'onSelectChartType',
      'change .onChangeAggType':  'onChangeAggType',
      'change .onChangeField':    'onChangeField',
      'click  .onUpdateAggResult': 'onUpdateAggResult',
      'click  .onAddSubAgg':       'onAddSubAgg',
      'click  .onRmSubAgg':        'onRmSubAgg'
    },

    getUIAggregations: function() { return this.getAncestorOfType('UIAggregations'); },

    onSelectChartType: function(evt) {
      var chartType = $(evt.target).val();
      this.getUIAggregations().getChartView().selectChartType(chartType);
    },

    onChangeAggType: function(evt) {
      var uiCard = $(evt.target).closest(".ui-card") ;
      var aggId = uiCard.attr("aggId");
      var type = uiCard.find("select[name=type]").val();
      var field = uiCard.find("select[name=field]").val();
      console.log("on change agg typei = " + type + ", field = " + field);
    },

    onChangeField: function(evt) {
      var uiCard = $(evt.target).closest(".ui-card") ;
      var aggId = uiCard.attr("aggId");
      var field = uiCard.find("select[name=field]").val();
      var aggs = this.aggContext.getAggregations();
      aggs.changeAggregationField(aggId, field);
      this.render();
    },

    onUpdateAggResult: function(evt) {
      this.getUIAggregations().updateAggResult();
    },

    onAddSubAgg: function(evt) {
      var uiCard = $(evt.target).closest(".ui-card") ;
      var type = uiCard.find("select[name=type]").val();
      this.getUIAggregations().addSubAgg(type);
      this.render();
    },

    onRmSubAgg: function(evt) {
      var uiCard = $(evt.target).closest(".ui-card");
      var aggId = uiCard.attr("aggId");
      this.getUIAggregations().removeSubAgg(aggId);
      this.render();
    }
  });

  return UICtrlPlugin ;
});
