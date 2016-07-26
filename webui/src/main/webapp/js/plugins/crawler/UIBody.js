define([
  'jquery',
  'underscore', 
  'backbone',
  'text!plugins/crawler/UIBody.jtpl'
], function($, _, Backbone,  Template) {
  var UIBody = Backbone.View.extend({
    el: $("#UIBody"),
    
    initialize: function () {
      _.bindAll(this, 'render') ;
      
    },
    
    _template: _.template(Template),

    render: function() {
      var params = { } ;
      $(this.el).html(this._template(params));
    },

    onActivate: function(evt) {
      this._loadUI('site/UISiteConfig');
    },

    events: {
      'click .onSelectUI': 'onSelectUI'
    },
    
    onSelectUI: function(evt) {
      var name = $(evt.target).closest('.onSelectUI').attr('name') ;
      this._loadUI(name);
    },

    _loadUI: function(name) {
      require(['plugins/crawler/' + name], function(UIDemoComponent) { 
        $('#UIWorkspace').empty();
        $('#UIWorkspace').unbind();
        UIDemoComponent.setElement($('#UIWorkspace')).render();
      }) ;
    }
  });
  
  return new UIBody() ;
});
