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
      "click     .onToggleMoreLessSection": "onToggleMoreLessSection",
      "click     .onAddTopHitSubAggregation": "onAddTopHitSubAggregation",
      
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
    
    onToggleMoreLessSection: function(evt) {
      var subAggBlock = $(evt.target).closest('.more-less-section-container');
      var subAggAddBlock = subAggBlock.find('.more-less-section');
      if(subAggAddBlock.css('display') == 'none') {
        subAggAddBlock.css('display', 'block');
        $(evt.target).text("less");
      } else {
        subAggAddBlock.css('display', 'none');
        $(evt.target).text("more");
      }
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
