define([
  'jquery',
  'underscore', 
  'backbone',
  'text!plugins/crawler/UIBody.jtpl'
], function($, _, Backbone,  Template) {
  var UIBody = Backbone.View.extend({
    el: $("#UIBody"),
    
    initialize: function () {
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
    },

    onActivate: function(evt) {
      this._loadUI('UICrawlerStatus');
    },

    events: {
      'click .onSelectUI': 'onSelectUI'
    },

    onSelectUI: function(evt) {
      var name = $(evt.target).closest('.onSelectUI').attr('name') ;
      this._loadUI(name);
    },

    _loadUI: function(name) {
      require(['plugins/crawler/' + name], function(uiComp) { 
        $('#UICrawlerWS').empty();
        $('#UICrawlerWS').unbind();
        uiComp.setElement($('#UICrawlerWS')).render();
      }) ;
    }
  });
  
  return new UIBody() ;
});
