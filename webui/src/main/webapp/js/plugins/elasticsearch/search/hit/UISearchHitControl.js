define([
  'jquery',
  'underscore', 
  'backbone',
  'text!plugins/elasticsearch/search/hit/UISearchHitControl.jtpl'
], function($, _, Backbone, Template) {

  var UISearchHitControl = Backbone.View.extend({
    label: 'Search Control',
    
    initialize: function (options) {
      this.uiSearchHit = options.uiSearchHit;
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { fieldStates: this.fieldStates } ;
      $(this.el).html(this._template(params));
    },

    events: {
      "click     .onToggleField": "onToggleField",
      "mouseover .field-block":   "onMouseOverFieldBlock",
      "mouseout  .field-block":   "onMouseOutFieldBlock"
    },

    onMouseOverFieldBlock: function(evt) {
      $(evt.target).closest('.field-block').find(".field-action-block").css("display", "block");
    },

    onMouseOutFieldBlock: function(evt) {
      $(evt.target).closest('.field-block').find(".field-action-block").css("display", "none");
    },

    onToggleField: function(evt) {
      var fieldBlock = $(evt.target).closest('.field-block');
      var fieldName = fieldBlock.attr('field');
      this.fieldStates[fieldName].toggle = !this.fieldStates[fieldName].toggle;
      if(this.fieldStates[fieldName].toggle) {
        fieldBlock.find(".field-action-block  span").text("del");
      } else {
        fieldBlock.find(".field-action-block  span").text("add");
      }
      this.uiSearchHit.uiSearchHitResult.render();
    },
    
    onResult: function(queryResult) {
      this.fieldStates = queryResult.fieldStates;
    }
  });
  
  return UISearchHitControl ;
});
