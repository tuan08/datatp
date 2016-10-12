define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil'
], function($, _, Backbone, UIUtil) {

  var UIView = Backbone.View.extend({

    mergeConfig: function(config) {
      var newConfig = {};
      if(this.defaultConfig) $.extend(true, newConfig, this.defaultConfig);
      if(this.config) $.extend(true, newConfig, this.config);
      if(config) $.extend(true, newConfig, config);
      return newConfig;
    },

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
