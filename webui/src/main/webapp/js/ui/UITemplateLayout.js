define([
  'jquery', 
  'underscore', 
  'backbone',
  'ui/UIView'
], function($, _, Backbone, UIView) {

  var UITemplateLayout = UIView.extend({
    initialize: function (options) {
      this.uiComponents = {};
      if(this.onInit) this.onInit(options);
    },

    _template: function() { throw Error("this method need to be implemented"); },

    render: function() {
      if(this.isModifiedModel()) this.onModifiedModel();
      var params = { label: this.label };
      $(this.el).html(this._template(params));
      for(var name in this.uiComponents) {
        var uiComp = this.uiComponents[name];
        var uiBlock = this.$("*[name=" + name + "]") ;
        uiComp.setElement(uiBlock).render();
      }
    },

    __getUICompoent: function(name) { return this.uiComponents[name] ; },

    __setUIComponent: function(name, uiComponent) { 
      uiComponent.uiParent = this;
      this.uiComponents[name] = uiComponent; 
    },
  });
  
  return UITemplateLayout ;
});
