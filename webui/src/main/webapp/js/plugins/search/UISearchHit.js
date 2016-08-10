define([
  'jquery',
  'underscore', 
  'backbone',
  'text!plugins/search/UISearchHit.jtpl'
], function($, _, Backbone, Template) {

  var UISearchHit = Backbone.View.extend({
    el: $("#UISearchHit"),
    
    initialize: function (options) {
      _.bindAll(this, 'render') ;
      var source = options.searchHit._source;
      this.attr = source.attr;
      this.content = {} ;
      if(source.entity != null && source.entity.content != null) {
        this.content = source.entity.content;
      } 
      console.printJSON(source);
    },

    _template: _.template(Template),

    render: function() {
      var params = { attr: this.attr, content: this.content } ;
      $(this.el).html(this._template(params));
    },

    events: {
      'click .onSelectPage': 'onSelectPage',
    }
  });
  
  return UISearchHit ;
});
