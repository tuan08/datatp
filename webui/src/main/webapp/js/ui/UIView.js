define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil'
], function($, _, Backbone, UIUtil) {

  var UIView = Backbone.View.extend({

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },

    getUIUtil: function() { return UIUtil; },

    isModifiedModel: function() { return this.modifiedModel == true ; },

    markModifiedModel: function(bool) { this.modifiedModel = bool; },

    onModifiedModel: function() {
      throw new Error("this method should be overrided and handle correctly");
    }

  });
  
  return UIView ;
});
