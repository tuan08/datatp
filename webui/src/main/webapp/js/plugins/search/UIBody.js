define([
  'jquery',
  'underscore', 
  'backbone',
  'plugins/search/UISearch',
], function($, _, Backbone, UISearch) {
  var UIBody = Backbone.View.extend({
    el: $("#UIBody"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
    },
    
    _template: _.template("<div id='UISearch'></div>"),

    render: function() {
      var params = { xdocQuery: this.xdocQuery } ;
      $(this.el).html(this._template(params));
    },

    onActivate: function(evt) {
      var uiSearch = new UISearch();
      uiSearch.setElement($('#UISearch')).render();
    }
  });
  
  return new UIBody() ;
});
