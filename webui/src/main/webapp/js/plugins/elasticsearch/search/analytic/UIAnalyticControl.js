define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/elasticsearch/search/analytic/ChartModel',
  'text!plugins/elasticsearch/search/analytic/UIAnalyticControl.jtpl'
], function($, _, Backbone, chart, Template) {
  var UIAnalyticControl = Backbone.View.extend({
    label: 'Analytic Control',
    
    initialize: function(options) {
      this.analyticContext = options.analyticContext;
    },
   
    _template: _.template(Template),

    render: function() {
      var params = { 
        chartModel:    this.analyticContext.getChartModel(),
        fieldsMapping: this.analyticContext.getESQueryContext().getIndexFieldsMapping().getFieldsMapping()
      } ;
      $(this.el).html(this._template(params));
    },

    events: {
      "click     .onRemoveSubAggregation": "onRemoveSubAggregation",
      "click     .onToggleUICollapsible": "onToggleUICollapsible",
      "click     .onAddTopHitSubAggregation": "onAddTopHitSubAggregation",
      "click     .onChangeDateHistogramChart": "onChangeDateHistogramChart",
      
      "mouseover .mouseover-action-block": "onMouseOverActionBlock",
      "mouseout  .mouseover-action-block": "onMouseOutActionBlock"
    },

    onMouseOverActionBlock: function(evt) {
      $(evt.target).closest('.mouseover-action-block').find(".action-block").css("display", "block");
    },

    onMouseOutActionBlock: function(evt) {
      $(evt.target).closest('.mouseover-action-block').find(".action-block").css("display", "none");
    },
    
    onRemoveSubAggregation: function(evt) {
      var fieldBlock = $(evt.target).closest('.mouseover-action-block');
      var fieldName = fieldBlock.attr('field');
      var chartModel = this.analyticContext.getChartModel();
      chartModel.rmSubAggregation(fieldName);
      this.uiParent.onChangeChartModel();
    },
    
    onToggleUICollapsible: function(evt) {
      var collapsibleBlk = $(evt.target).closest('.ui-collapsible');
      var collapsibleSectionBlk = collapsibleBlk.find('.ui-collapsible-section');
      if(collapsibleSectionBlk.css('display') == 'none') {
        collapsibleSectionBlk.css('display', 'block');
        $(evt.target).text("less");
      } else {
        collapsibleSectionBlk.css('display', 'none');
        $(evt.target).text("more");
      }
    },

    onChangeDateHistogramChart: function(evt) {
      var chartInputBlk = $(evt.target).closest('.ui-collapsible-section');
      var dateField = chartInputBlk.find("select[name='dateField']").val()
      var interval = chartInputBlk.find("input[name='interval']").val()
      var format = chartInputBlk.find("input[name='format']").val()

      var chartModel = this.analyticContext.getChartModel();
      chartModel.setDateField(dateField);
      chartModel.setInterval(interval);
      chartModel.setFormat(format);
      this.uiParent.onChangeChartModel();
    },
    
    onAddTopHitSubAggregation: function(evt) {
      var addTopHitBlock = $(evt.target).closest('.add-top-hit-sub-aggregation');
      var name = addTopHitBlock.find("input.name").val();
      var selectField = addTopHitBlock.find("select.field").find(":selected").text();
      var size = addTopHitBlock.find("input.size").val();
      if(name == "") name = selectField;
      
      var chartModel = this.analyticContext.getChartModel();
      chartModel.addSubAggregation(new chart.aggregation.TermsAggregation(name, selectField, size));
      this.uiParent.onChangeChartModel();
    },
    
    onSearch: function(analyticContext) {
      this.analyticContext = analyticContext;
    }
  });
  
  return UIAnalyticControl ;
});
