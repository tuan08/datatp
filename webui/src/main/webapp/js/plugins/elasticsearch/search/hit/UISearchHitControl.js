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
      "click     .field-action-block": "onToggleField",
      "mouseover .field-block":   "onMouseOverFieldBlock",
      "mouseout  .field-block":   "onMouseOutFieldBlock"
    },

    onMouseOverFieldBlock: function(evt) {
      $(evt.target).closest('.field-block').find(".field-action-block").css("display", "inline-block");
    },

    onMouseOutFieldBlock: function(evt) {
      $(evt.target).closest('.field-block').find(".field-action-block").css("display", "none");
    },

    onToggleField: function(evt) {
      var fieldBlock = $(evt.target).closest('.field-block');
      var fieldName = fieldBlock.attr('field');
      this.fieldStates[fieldName].toggle = !this.fieldStates[fieldName].toggle;
      if(this.fieldStates[fieldName].toggle) {
        fieldBlock.find(".ui-action > span").removeClass("ui-icon-plusthick").addClass("ui-icon-trash");
      } else {
        fieldBlock.find(".ui-action > span").removeClass("ui-icon-trash").addClass("ui-icon-plusthick");
      }
      this.uiSearchHit.uiSearchHitResult.render();
    },
    
    onResult: function(queryResult) {
      this.fieldStates = queryResult.fieldStates;
    }
  });
  
  return UISearchHitControl ;
});
