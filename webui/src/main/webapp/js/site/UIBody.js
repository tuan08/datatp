define([
  'jquery',
  'underscore', 
  'backbone',
], function($, _, Backbone) {
  var UIBody = Backbone.View.extend({
    el: $("#UIBody"),
    
    initialize: function () {
    },
    
    render: function() {
      this.selectPlugin("uidemo");
    },
    
    selectPlugin: function(name) {
      var UIBody = this;
      require(['plugins/' + name + '/UIBody'], function(UIBodyComponent) { 
        UIBody.setUIBody(UIBodyComponent);
      }) ;
    },

    setUIBody: function(uicomp) {
      this.uicomponent  = uicomp ;
      $(this.el).empty();
      $(this.el).unbind();
      uicomp.setElement($('#UIBody')).render();
      if(uicomp.onActivate != undefined) {
        uicomp.onActivate();
      }
    }
  });
  
  return new UIBody() ;
});
