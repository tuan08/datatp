define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIUtil'
], function($, _, Backbone, UIUtil) {

  var UITemplateLayout = Backbone.View.extend({
    initialize: function (options) {
      this.uiComponents = {};
      if(this.onInit) this.onInit(options);
    },

    getAncestorOfType: function(type) { return UIUtil.getAncestorOfType(this, type) ; },

    _template: function() {
      throw Error("this method need to be implemented");
    },

    render: function() {
      var params = { label: this.label };
      $(this.el).html(this._template(params));
      for(var name in this.uiComponents) {
        var uiComp = this.uiComponents[name];
        var uiBlock = this.$("*[name=" + name + "]") ;
        uiComp.setElement(uiBlock).render();
      }
    },

    __getUICompoent: function(name) { return this.uiComponents[name] ; },

    __setUIComponent: function(name, uiComponent) { this.uiComponents[name] = uiComponent; },
  });
  
  return UITemplateLayout ;
});
